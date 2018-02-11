package us.malfeasant.admiral64;

import us.malfeasant.admiral64.timing.Oscillator;
import us.malfeasant.admiral64.timing.Powerline;

public class Configuration {
	public final Oscillator oscillator;
	public final Powerline powerline;
	public final String name;
	
	/**
	 *	Default settings
	 */
	public Configuration() {
		oscillator = Oscillator.NTSC;
		powerline = Powerline.NA;
		name = "Admiral 64";
	}
	
	// TODO: more options, dialog?
}
