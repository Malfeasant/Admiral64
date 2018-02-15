package us.malfeasant.admiral64.machine;

import us.malfeasant.admiral64.Configuration;

/**
 *	This class will encompass the entire simulation, minus the gui bits and thread management.  Ideally, it shouldn't
 *	care what thread runs it, assuming it's one, and shouldn't care whether its gui is JavaFX or Swing or something else.
 */
public class Machine {
	private final Configuration configuration;
	
	public Machine(Configuration conf) {
		configuration = conf;
	}
	
	/**
	 *	Tick the RTC
	 */
	public void tick() {
		
	}
	
	/**
	 *	Run a θ2 cycle
	 */
	public void cycle() {
		
	}
}
