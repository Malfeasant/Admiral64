package us.malfeasant.admiral64.machine.vic;

import us.malfeasant.admiral64.worker.VideoOut;

public class Vic {
	public enum Flavor {
		MOS6567R56A, MOS6567R8, MOS6569;
	}
	private final Flavor flavor;
	private final VideoOut videoOut;
	
	private boolean hBorder;
	private boolean vBorder;
	private boolean dispEnable;
	
	private boolean hSync;
	private boolean vSync;
	
	private boolean hBlank;
	private boolean vBlank;
	
	private int rasterCycle;
	private int rasterLine;
	
	public Vic(Flavor f, VideoOut vOut) {
		flavor = f;
		videoOut = vOut;
	}
	public void cycle() {
		rasterCycle++;
		if (rasterCycle > 64) {
			rasterCycle = 0;
			rasterLine++;
			if (rasterLine > 310) {
				rasterLine = 0;
			}
		}
		videoOut.send(rasterCycle * 8, rasterLine, rasterCycle / 65);
	}
}
