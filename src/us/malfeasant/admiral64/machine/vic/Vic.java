package us.malfeasant.admiral64.machine.vic;

import us.malfeasant.admiral64.console.FrameBuffer;
import us.malfeasant.admiral64.machine.bus.Bus;
import us.malfeasant.admiral64.machine.bus.Peekable;
import us.malfeasant.admiral64.machine.bus.Pokeable;
import us.malfeasant.admiral64.timing.CrystalConsumer;

public class Vic implements CrystalConsumer, Peekable, Pokeable {
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
	
	private static final int RESET_X = 21;	// Cycle to reset display coordinate to 0 as well as address counters (vc, vmli)
	private static final int INC_Y = 4;	// Cycle to increment raster register
	
	private final FrameBuffer pixelBuffer;
	private final BusControl control = new BusControl();	// manages ba, aec
	
	// Used for smooth scrolling
	private boolean csel = true;	// shrinks horizontal borders by 16px/2chars
	private boolean rsel = true;	// shrinks vertical borders by 8px/1char
	private int xscroll;	// Horizontal offset 0-7
	private int yscroll = 3;	// Vertical offset 0-7
	
	private boolean hBorder;
	private boolean vBorder;
	private boolean dispEnable = true;
	private boolean denFrame;	// sampling of dEn at line 0x30, used for triggering badlines
	
	private int currentCycle;
	private int rasterX;	// x coordinate
	private int rasterY;	// y coordinate
	
	// mode bits:
	private boolean bmm;	// 0 = text, 1 = bitmap 
	private boolean mcm;	// multicolor
	private boolean ecm;	// extended color
	
	private int borderColor = 0xe;	// TODO: only setting this here for debug
	private final int[] backColor = new int[] {
			6, 0, 0, 0,	// TODO: defaults?
	};
	
	private boolean vActive;	// if graphics sequencer is active- goes inactive outside viewing area
	private boolean hActive;	// same, but within lines
	
	// Used for address generation
	private int vc;	// video counter
	private int vcBase;
	private int rc;
	
	private int vm;	// 4 bits video matrix pointer- i.e. where text screen memory is located
	private int cb;	// 3 bits character base- i.e. base of char pointers in text mode, top bit base of bitmap screen
	
	private final short[] lineBuffer = new short[40];	// stores character pointers and color ram between bad lines
	private int vmli;	// index into above
	
	private long delay;	// ring buffers graphics pixels (but not sprites or border/bg transitions)- 4-bit pixels are
	// put in LSN after shifting left. Pixels are taken from a position adjusted by horizontal smooth scroll offset.
	private int pixels;	// ring buffers all pixels before passing block of 8 to framebuffer
	
	public Vic(Flavor f) {
		flavor = f;
		pixelBuffer = new FrameBuffer(f.cyclesPerLine, f.linesPerField);
	}
	
	private Bus.VicBus bus;
	
	public void connectBus(Bus.VicBus b) {
		bus = b;
	}
	
	private void emitPixel(int p) {	// splitting this out since it needs to be done in both half cycles
		assert ((p & 0xf) == p) : "Bad pixel: " + p + "\nMust be 4 bits.";
		pixels = pixels << 4 | p;
	}
	
	private void halfCycle() {	// anything that happens the same on every half cycle...
		
		// yes, these are checked every cycle:
		if (rasterY == 0x30) denFrame |= dispEnable;	// see case 0x30 of raster switch for explanation
		boolean badline = denFrame & (rasterY >= 0x30) & (rasterY <= 0xf7) & (rasterY & 7) == yscroll;
		vActive |= badline;
		
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
		case 2:
			if (rc==7) {
				vcBase = vc;
				if (!badline) vActive = false;
			} else rc++;
			break;
		default:	// all the crap that can't get its own case
			hActive = currentCycle <= 61 & currentCycle >= 22;
			control.setStun(BusControl.CHAR_BA, currentCycle <= 61 & currentCycle >= 19 & badline);	// enable character fetches
			if (currentCycle == flavor.cyclesPerLine - 1) {	// can't make a non-constant case...
				// check if sprite 0 is enabled, if so ba--;
			}
			if (hActive) {
				if (badline) {	// this really should come after gdata, but causes buffer overrun- gotta figure it out
					lineBuffer[vmli] = (short) bus.read12((vm << 10) | vc);
				}
				//cdata = vActive ? lineBuffer[vmli++] : 0;	// and increment the index
				//int addr = vActive ? bmm ? (cb >> 2) << 13 | vc << 3 | rc : cb << 10 | (cdata & 0xff) << 3 | rc : 0x3fff;
				// if bitmap mode, cb high bit points to one of two 8k pages, vc is used directly
				// as an index into that page, with rc added after
				// if text mode, cb picks one of 8 2k character generator blocks, then 8-bit char pointer picks
				// a character, rc selects which row of the character gets displayed.
				// if inactive, address floats to 0x3fff
				//if (ecm) addr &= 0x39ff;	// ecm sacrifices some characters for more colors
				//gdata = bus.read8(addr);
			}
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
			int pixel = 0;	// border or valid modes will overwrite this
			boolean foreground;	// if this pixel is distinguished from the background for collisions, priority
			// because multicolor mode is weird- 00, 01 are background, 10, 11 are foreground
			
			
			// overrides all others, don't bother calculating any other graphics or collisions if border is shown:
			if (vBorder || hBorder) pixel = borderColor;	// this also covers the display disabled case
			else {
				
				// background graphics:
				
				
				// sprites:
				
			}
			
			if (badline & (i & 1) != 0) pixel = 0xf;	// debug
			if (hActive & i == 0) pixel = 0xc;	// more debug
			
			assert pixel == (pixel & 0xf) : "Invalid pixel value " + pixel;
			pixels = (pixels << 4) | pixel;
		}
		pixelBuffer.set(rasterX, rasterY, pixels);
	}
	
	/**
	 * Query the AEC line- expected to be called by (on behalf of?) CPU
	 * @return status of AEC signal- false: cpu can not affect the bus
	 */
	public boolean getAEC() {
		return control.getAEC();
	}
	
	/**
	 * Query the BA line- expected to be called by (on behalf of?) CPU
	 * @return status of BA signal- false: cpu stops read immediately, write completes within 3 cycles
	 */
	public boolean getBA() {
		return control.getBA();
	}
	
	public FrameBuffer getFrameBuffer() {
		return pixelBuffer;
	}

	@Override
	public void poke(int addr, int data) {
		switch (addr & 0x3f) {	// 1k block, but vic only looks at lowest 6 bits, so registers repeat every 64b
		// TODO: 0-10 sprite positions
		case 0x11:	// 53265
			yscroll = (data & 7);
			rsel = (data & 8) > 0;
			dispEnable = (data & 0x10) > 0;
			bmm = (data & 0x20) > 0;
			ecm = (data & 0x40) > 0;
			// TODO: bit 7 is high bit of raster compare reg
			break;
		case 0x12:	// 53266
			// TODO raster compare reg
			break;
		case 0x13:	// 53267
		case 0x14:	// 53268
			// Light pen- write likely has no effect
			break;
		case 0x15:	// 53269
			// TODO: sprite enable
			break;
		case 0x16:	// 53270
			xscroll = (data & 7);
			csel = (data & 8) > 0;
			mcm = (data & 0x10) > 0;
			// bit 5 = reset- no apparent effect
			// bit 6/7 unused
			break;
		case 0x17:	// 53271
			// TODO sprite y expand
			break;
		case 0x18:	// 53272
			// bit 0 unused
			cb = (data >> 1) & 7;	// character base pointer
			vm = data >> 4;	// video matrix pointer
			break;
		case 0x19:	// 53273
			// TODO: interrupt source bits
			break;
		case 0x1a:	// 53274
			// TODO: interrupt mask
			break;
		case 0x1b:	// 53275
			// TODO: sprite to foreground priority
			break;
		case 0x1c:	// 53276
			// TODO: sprite multicolor mode
			break;
		case 0x1d:	// 53277
			// TODO: sprite x expand
			break;
		case 0x1e:	// 53278
			// TODO: sprite to sprite collision
			break;
		case 0x1f:	// 53279
			// TODO: sprite to foreground collision
			break;
		default:	// the rest are color registers so get special handling
			colorPoke(addr, data);
		}
	}
	
	private void colorPoke(int addr, int data) {	// helper- avoids having & 0xf everywhere
		data &= 0xf;
		switch (addr) {
		case 0x20:	// 53280
			borderColor = data;
			break;
		case 0x21:	// 53281
		case 0x22:	// 53282	used for bitmap multicolor & extended color
		case 0x23:	// 53283	used for bitmap multicolor & extended color
		case 0x24:	// 53284	used for extended color
			backColor[addr - 0x21] = data;
			break;
		// TODO: 25-26 sprite mc
		// TODO: 27-2e sprite color
		default:	// the rest are not connected, write does nothing
		}
	}
	
	@Override
	public int peek(int addr) {
		int data = 0;
		switch (addr & 0x3f) {	// 1k block, but vic only looks at lowest 6 bits, so registers repeat every 64b
		// TODO: 0-10 sprite positions
		case 0x11:	// 53265
			data = (ecm ? 0x40 : 0) | (bmm ? 0x20 : 0) | (dispEnable ? 0x10 : 0) | (rsel ? 8 : 0) | yscroll;
			// TODO: bit 7 is high bit of raster reg
			break;
		case 0x12:	// 53266
			// TODO raster reg
			break;
		case 0x13:	// 53267
			// TODO: Light pen x
			break;
		case 0x14:	// 53268
			// TODO: Light pen y
			break;
		case 0x15:	// 53269
			// TODO: sprite enable
			break;
		case 0x16:	// 53270
			data = 0xc0 | (mcm ? 0x10 : 0) | (csel ? 8 : 0) | xscroll;
			// bit 6/7 unused- bit 5 is reset, unclear if that gets used anywhere... TODO?
			break;
		case 0x17:	// 53271
			// TODO sprite y expand
			break;
		case 0x18:	// 53272
			data = (vm << 4) | (cb << 1) | 1;
			// bit 0 unused, always reads 1
			break;
		case 0x19:	// 53273
			// TODO: interrupt source bits
			break;
		case 0x1a:	// 53274
			// TODO: interrupt mask
			break;
		case 0x1b:	// 53275
			// TODO: sprite to foreground priority
			break;
		case 0x1c:	// 53276
			// TODO: sprite multicolor mode
			break;
		case 0x1d:	// 53277
			// TODO: sprite x expand
			break;
		case 0x1e:	// 53278
			// TODO: sprite to sprite collision
			break;
		case 0x1f:	// 53279
			// TODO: sprite to foreground collision
			break;
		default:	// the rest are color registers so get special handling
			data = colorPeek(addr);
		}
		return data;
	}
	
	private int colorPeek(int addr) {
		int data = 0;
		switch (addr) {
		case 0x20:	// 53280
			data = borderColor;
			break;
		case 0x21:	// 53281
		case 0x22:	// 53282	used for bitmap multicolor & extended color
		case 0x23:	// 53283	used for bitmap multicolor & extended color
		case 0x24:	// 53284	used for extended color
			backColor[addr - 0x21] = data;
			break;
		// TODO: 25-26 sprite mc
		// TODO: 27-2e sprite color
		default:	// the rest are not connected, reads return f
			data = 0xf;
		}
		return data | 0xf0;
	}
	
	@Override
	public void negEdge() {	// marks the beginning of a cycle- counters update here
		control.negEdge();	// update ba/aec logic
		currentCycle++;
		if (currentCycle >= flavor.cyclesPerLine) currentCycle = 0;
		rasterX++;
	}
	
	@Override
	public void posEdge() {
		control.posEdge();	// update ba/aec logic
	}
}
