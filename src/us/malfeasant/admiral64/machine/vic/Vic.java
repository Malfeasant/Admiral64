package us.malfeasant.admiral64.machine.vic;

import java.util.function.Consumer;

public class Vic {
	public enum Flavor {
		MOS6567R56A, MOS6567R8, MOS6569;
	}
	private final Flavor flavor;
	
	private Consumer<VideoEvent> videoOut;
	
	private boolean hBorder;
	private boolean vBorder;
	private boolean dispEnable;
	
	private boolean hSync;
	private boolean vSync;
	
	private boolean hBlank;
	private boolean vBlank;
	
	private int rasterCycle;
	private int rasterLine;
	
	public Vic(Flavor f) {
		flavor = f;
	}
	public void cycle() {
		VideoEvent out = VideoEvent.values()[rasterCycle / 8];
		rasterCycle++;
		if (rasterCycle > 64) {
			rasterCycle = 0;
			rasterLine++;
			out = VideoEvent.HSYNC;
			if (rasterLine > 310) {
				rasterLine = 0;
				out = VideoEvent.VSYNC;
			}
		}
		videoOut.accept(out);
	}
	
	public void connectVideo(Consumer<VideoEvent> v) {
		videoOut = v;
	}
}
