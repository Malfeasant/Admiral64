package us.malfeasant.admiral64.machine.vic;

import us.malfeasant.admiral64.console.FrameBuffer;
import us.malfeasant.admiral64.machine.bus.Bus;
import us.malfeasant.admiral64.timing.CrystalConsumer;

public class Vic implements CrystalConsumer {
	/**
	 * Thanks to http://www.unusedino.de/ec64/technical/misc/vic656x/vic656x.html for magic numbers
	 * However, I'm fudging things a little- the 3 chips have different cycle lengths, and as described there,
	 * the "first" cycle is when the raster register is updated- problem is then the cycle numbers
	 * where certain things happen are shifted around.  To simplify, I'm taking cycle 0 to be 2 cycles before 
	 * the Sprite 0 pointer is fetched, this way the only differences between the flavors are the number of
	 * idle cycles at the end.
	 */
	public enum Flavor {
		MOS6567R56A(64, 262), MOS6567R8(65, 263), MOS6569(63, 312);
		public final int cyclesPerLine;	// Several classes need these dimensions
		public final int linesPerField;
		Flavor(int cpl, int lpf) {
			cyclesPerLine = cpl;
			linesPerField = lpf;
		}
	}
	private final Flavor flavor;
	
	private static final int RESET_X = 17;	// Cycle to reset display coordinate to 0	TODO: verify
	private static final int INC_Y = 4;	// Cycle to increment raster register
	
	private final FrameBuffer pixelBuffer;
	
	// Used for smooth scrolling
	private boolean csel;	// shrinks horizontal borders by 16px/2chars
	private boolean rsel;	// shrinks vertical borders by 8px/1char
	private int xscroll;	// Horizontal offset 0-7
	private int yscroll;	// Vertical offset 0-7
	
	private boolean hBorder;
	private boolean vBorder;
	private boolean dispEnable = true;
	private boolean denFrame;	// sampling of dEn at line 0x30, used for triggering badlines
	
	private int currentCycle;
	private int rasterX;	// x coordinate
	private int rasterY;	// y coordinate
	
	private int borderColor = 0xe;	// TODO: only setting this here for debug
	private int backColor = 0x6;	// TODO: there are actually 4 background registers...
	
	private int ba = 3;	// Bus Available signal
	// not a simple flag since it has to be cleared for 3 cycles (worst case) before the CPU stops
	// 3 indicates the cpu has the bus, counts down from there.  0 indicates the vic has the bus.
	// anything in between means the cpu has the bus, will finish a write but will not start a new cycle
	
	// Used for address generation
	private int vc;	// video counter
	private int vcBase;
	private int rc;
	
	private final short[] lineBuffer = new short[40];	// stores character pointers and color ram between bad lines
	private int vmli;	// index into above
	
	public Vic(Flavor f) {
		flavor = f;
		pixelBuffer = new FrameBuffer(f.cyclesPerLine, f.linesPerField);
	}
	
	private Bus.VicBus bus;
	
	public void connectBus(Bus.VicBus b) {
		bus = b;
	}
	
	@Override
	public void cycle() {
		rasterX++;
		currentCycle++;
		if (currentCycle >= flavor.cyclesPerLine) currentCycle = 0;
		
		// yes, these are checked every cycle:
		if (rasterY == 0x30) denFrame |= dispEnable;	// see case 0x30 of raster switch for explanation
		boolean badline = denFrame & (rasterY >= 0x30) & (rasterY <= 0xf7) & (rasterY & 7) == yscroll;
		
		int packed = 0;
		
		switch (currentCycle) {	// Rough matches- TODO: add when to assert BA for sprites &
		case RESET_X:
			rasterX = 0;
			vc = vcBase;
			vmli = 0;
			if (badline) rc = 0;
			break;
		case INC_Y:
			rasterY++;
			if (rasterY >= flavor.linesPerField) {
				rasterY = 0;	// end of field, reset y counter
				vcBase = 0;
			}
			// TODO: raster interrupt register compare
			switch (rasterY) {
			case 247:
				if (!rsel) vBorder = true;	// logically distinct from vBorder = !rsel
				break;
			case 251:
				if (rsel) vBorder = true;
				break;
			case 55:
				if (!rsel & dispEnable) vBorder = false;
				break;
			case 51:
				if (rsel & dispEnable) vBorder = false;
				break;
			case 0x30:
				denFrame = dispEnable;	// this is complicated...
				// A Bad Line Condition can only occur if the DEN bit has been set for at
				// least one cycle somewhere in raster line $30 (see section 3.5.).
				// so it has to be checked every cycle...
				break;
			}
			break;
		case 58:	// TODO fudge
			if (rc==7) vcBase = vc;
			else rc++;
			break;
		default:	// all the crap that can't get its own case
			if (currentCycle == flavor.cyclesPerLine - 1) {	// can't make a non-constant case...
				// check if sprite 0 is enabled, if so ba--;
			} else if (currentCycle <= 55) {	// TODO: fudge these numbers
				if (currentCycle >= 12 & badline) ba = (ba == 0) ? 0 : ba - 1;	// enable character fetches
				if (currentCycle >= 15) {	// TODO: more fudging...
					if (badline) {
						lineBuffer[vmli] = (short) bus.read12(0);	// TODO: address calculation
					}
					int c = lineBuffer[vmli];
					int g = bus.read8(0);	// TODO: address calculation (will use c- depends on mode)
				}
			}
			if (ba < 0) ba = 0;
		}
		
		for (int i = 0; i < 8; i++) {
			switch (rasterX * 8 + i) {	// Fine matches
			case 35:
				if (!csel) hBorder = false;
				break;
			case 28:
				if (csel) hBorder = false;
				break;
			case 339:
				if (!csel) hBorder = true;
				break;
			case 348:
				if (csel) hBorder = true;
				break;
			}
			int pixel = vBorder || hBorder ? borderColor : backColor;
			if (badline) pixel = 0xf;
			assert pixel == (pixel & 0xf) : "Invalid pixel value " + pixel;
			packed = (packed << 4) | pixel;
		}
		pixelBuffer.set(rasterX, rasterY, packed);
	}
	
	public FrameBuffer getFrameBuffer() {
		return pixelBuffer;
	}
}
