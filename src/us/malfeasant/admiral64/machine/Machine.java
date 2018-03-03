package us.malfeasant.admiral64.machine;

import java.util.function.Consumer;

import us.malfeasant.admiral64.Configuration;
import us.malfeasant.admiral64.machine.vic.Vic;
import us.malfeasant.admiral64.machine.vic.Pixels;

/**
 *	This class will encompass the entire simulation, minus the gui bits and thread management.  Ideally, it shouldn't
 *	care what thread runs it, assuming it's one, and shouldn't care whether its gui is JavaFX or Swing or something else.
 */
public class Machine {
	private final Vic vic;
	
	public Machine(Configuration conf) {
		vic = new Vic(conf.vicFlavor);
	}
	
	public void connectVideo(Consumer<Pixels> v) {
		vic.connectVideo(v);
	}
	
	/**
	 *	Tick the RTC
	 */
	public void tick() {
		
	}
	
	/**
	 *	Run some θ2 cycles
	 */
	public void cycle(int times) {
		for (int i = 0; i < times; i++) {
			vic.cycle();
		}
	}
}
