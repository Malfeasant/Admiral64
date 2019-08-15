package us.malfeasant.admiral64.machine.vic;

import us.malfeasant.admiral64.console.FrameBuffer;

public class Vic {
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
	
	private final FrameBuffer pixelBuffer;
	
	private final int vBottom = 251;	//	TODO: dependent on csel- alt 247
	private final int vTop = 51;	//	TODO: dependent on csel- alt 55
	
	private boolean hBorder;
	private boolean vBorder;
	private boolean dispEnable = true;
	
	private int rasterCycle;
	private int rasterLine;
	
	private int borderColor = 0xe;
	private int backColor = 0x6;	// TODO: there are actually 4 background registers...
	
	public Vic(Flavor f) {
		flavor = f;
		pixelBuffer = new FrameBuffer(f.cyclesPerLine, f.linesPerField);
	}
	
	public void cycle() {
		rasterCycle++;
		int packed = 0;
		for (int x = 0; x < 8; x++) {
			int pixel = 0;
			switch (rasterCycle * 8 + x) {
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
*/			case 28:
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
			}
			if (vBorder || hBorder) {
				pixel = borderColor;
			} else {
				pixel = backColor;
			}
			assert pixel == (pixel & 0xf) : "Invalid pixel value " + pixel;
			packed = (packed << 4) | pixel;
		}
		pixelBuffer.set(rasterCycle, rasterLine, packed);
	}
	
	public FrameBuffer getFrameBuffer() {
		return pixelBuffer;
	}
}
