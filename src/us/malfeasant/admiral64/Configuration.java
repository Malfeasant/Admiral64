package us.malfeasant.admiral64;

import us.malfeasant.admiral64.machine.bus.BuiltInROMs;
import us.malfeasant.admiral64.machine.vic.Vic;
import us.malfeasant.admiral64.timing.Oscillator;
import us.malfeasant.admiral64.timing.Powerline;

public class Configuration {
	public final Vic.Flavor vicFlavor;
	public final Oscillator oscillator;
	public final Powerline powerline;
	public final String name;
	public final BuiltInROMs basicRom;
	public final BuiltInROMs charRom;
	public final BuiltInROMs kernalRom;
	
	/**
	 *	Default settings
	 */
	public Configuration() {
		vicFlavor = Vic.Flavor.MOS6567R8;
		oscillator = Oscillator.NTSC;
		powerline = Powerline.NA;
		name = "Admiral 64";
		basicRom = BuiltInROMs.BASIC;
		kernalRom = BuiltInROMs.KERNAL3;	// most common
		charRom = BuiltInROMs.CHAR;
	}
	
	// TODO: more options, dialog?
}
