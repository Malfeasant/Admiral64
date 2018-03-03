package us.malfeasant.admiral64.machine.vic;

import java.util.function.Consumer;

public class Vic {
	public enum Flavor {
		MOS6567R56A, MOS6567R8, MOS6569;
	}
	private final Flavor flavor;
	
	private Consumer<Pixels> videoOut;
	
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
		
	}
	
	public void connectVideo(Consumer<Pixels> v) {
		videoOut = v;
	}
}
