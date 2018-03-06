package us.malfeasant.admiral64.machine.vic;

import java.util.function.Consumer;

public class Vic {
	public enum Flavor {
		MOS6567R56A, MOS6567R8, MOS6569;
	}
	private final Flavor flavor;
	
	private Consumer<Pixels> videoOut;
	
	private final int vBottom = 251;	//	TODO: dependent on csel- alt 247
	private final int vTop = 51;	//	TODO: dependent on csel- alt 55
	
	private boolean hBorder;
	private boolean vBorder;
	private boolean dispEnable = true;
	
//	private boolean hSync;
//	private boolean vSync;
	
	private boolean hBlank;
	private boolean vBlank;
	
	private int rasterCycle;
	private int rasterLine;
	
	private byte borderColor = 0xe;
	private byte backColor = 0x6;	// TODO: there are actually 4 background registers...
	
	public Vic(Flavor f) {
		flavor = f;
	}
	
	public void cycle() {
		rasterCycle++;
		Pixels.Builder pixels = new Pixels.Builder();
		for (int x = 0; x < 8; x++) {
			byte pixel = 0;
			switch (rasterCycle * 8 + x) {
/*			case 416:
				hSync = true;
				break;
			case 452:
				hSync = false;
				break;
*/			case 396:
				hBlank = true;
				break;
			case 496:
				hBlank = false;
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
/*				case 17:
					vSync = true;
					break;
				case 20:
					vSync = false;
					break;
*/				}
			}
			if (!vBlank && !hBlank) {	// otherwise nothing displays
				if (vBorder || hBorder) {
					pixel = borderColor;
				} else {
					pixel = backColor;
				}
			}
			pixels.setColorAt(x, pixel);
		}
		if (!vBlank && !hBlank) {
			videoOut.accept(pixels.build(rasterCycle, rasterLine));
		}
	}
	
	public void connectVideo(Consumer<Pixels> v) {
		videoOut = v;
	}
}
