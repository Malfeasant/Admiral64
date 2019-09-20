package us.malfeasant.admiral64.machine.vic;

import us.malfeasant.admiral64.console.FrameBuffer;
import us.malfeasant.admiral64.timing.CrystalConsumer;

public class Vic implements CrystalConsumer {
	/**
	 * Thanks to http://www.unusedino.de/ec64/technical/misc/vic656x/vic656x.html for magic numbers
	 * However, I'm fudging things a little- the 3 chips have different cycle lengths, and as described there,
	 * the "first" cycle is when the raster register is updated- problem is then the cycle numbers
	 * where certain things happen are shifted around.  To simplify, I'm taking cycle 0 to be the time 
	 * when the Sprite 0 pointer is fetched, this way the only differences between the flavors are the number of
	 * idle cycles at the end.  One challenge- asserting BA 3 cycles before, since that will be a different cycle
	 * for each flavor... but I'll figure something out.
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
	
	private final int RESET_X = 18;	// Cycle to reset x coordinate to 0	TODO: verify
	private final int INC_Y = 6;	// Cycle to increment raster register
	
	private final FrameBuffer pixelBuffer;
	
	private final int vBottom = 251;	//	TODO: dependent on csel- alt 247
	private final int vTop = 51;	//	TODO: dependent on csel- alt 55
	
	private boolean hBorder;
	private boolean vBorder;
	private boolean dispEnable = true;
	
	private int currentCycle;
	private int rasterX;	// x coordinate
	private int rasterY;	// y coordinate
	
	private int borderColor = 0xe;
	private int backColor = 0x6;	// TODO: there are actually 4 background registers...
	
	public Vic(Flavor f) {
		flavor = f;
		pixelBuffer = new FrameBuffer(f.cyclesPerLine, f.linesPerField);
	}
	
	@Override
	public void cycle() {
		currentCycle++;
		if (currentCycle >= flavor.cyclesPerLine) currentCycle = 0;
		
		int packed = 0;
		
		switch (currentCycle) {	// Rough matches- TODO: add when to assert BA for sprites & 
		case RESET_X:
			rasterX = 0;
			break;
		case INC_Y:
			rasterY++;
			if (rasterY >= flavor.linesPerField) rasterY = 0;	// end of field, reset y counter
			switch (rasterY) {
			case vBottom:
				vBorder = true;
				break;
			case vTop:
				vBorder = !dispEnable;
				break;
			}
			break;
		}
		
		/*for (int i = 0; i < 8; i++) {
			switch (rasterX) {	// Fine matches
/*			case 496:
				// ba for char fetch
				break;
			case 35:
				// if (!csel) {
				//	hBorder = false;
				//	if (rasterLine == ybottom) vBorder = true;
				// }
				break;
			case 339:
				// if (!csel) hBorder = true;
				break;
			case 28:
				// if csel
				hBorder = false;
				if (rasterLine == vBottom) vBorder = true;
				if (rasterLine == vTop && dispEnable) vBorder = false;
				break;
			case 348:
				// if csel
				hBorder = true;
				break;
			case 412:	// actually anywhere between 404 and 412
				rasterLine++;
				break;
			case 12:
				// enable character fetch
				break;
			case 332:
				// disable character fetch
				// disable ba for char fetch
				break;
			case 336:
				// ba for sprite 0
				break;
			case 376:
				// end ba for sprite 0
				break;
			case 519:
				rasterCycle = 0;
				switch (rasterLine) {
				case vBottom:
					vBorder = true;
					break;
				case vTop:
					vBorder = !dispEnable;
					break;
				case 262:
					rasterLine = 0;
					break;
				}
			}*/
			int pixel = vBorder || hBorder ? borderColor : backColor;
			assert pixel == (pixel & 0xf) : "Invalid pixel value " + pixel;
			//packed = (packed << 4) | pixel;
			packed = pixel * 0x11111111;	// duplicate to all pixels in block
			rasterX++;
//		}
		pixelBuffer.set(rasterX, rasterY, packed);
	}
	
	public FrameBuffer getFrameBuffer() {
		return pixelBuffer;
	}
}
