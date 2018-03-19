package us.malfeasant.admiral64;

import us.malfeasant.admiral64.machine.bus.BuiltInROMs;
import us.malfeasant.admiral64.machine.vic.Vic;
import us.malfeasant.admiral64.timing.Oscillator;
import us.malfeasant.admiral64.timing.Powerline;

public class Configuration {
	public enum Quick {
		OLD_PAL(
				Vic.Flavor.MOS6569,
				Oscillator.PAL,
				Powerline.EU,
				BuiltInROMs.KERNAL2
				),
		OLD_NTSC(
				Vic.Flavor.MOS6567R56A,
				Oscillator.NTSC,
				Powerline.NA,
				BuiltInROMs.KERNAL2
				),
		NEW_PAL(
				Vic.Flavor.MOS6569,
				Oscillator.PAL,
				Powerline.EU,
				BuiltInROMs.KERNAL3
				),
		NEW_NTSC(
				Vic.Flavor.MOS6567R8,
				Oscillator.NTSC,
				Powerline.NA,
				BuiltInROMs.KERNAL3
				);
		
		final Vic.Flavor vicFlavor;
		final Oscillator oscillator;
		final Powerline powerline;
		final BuiltInROMs basicRom;
		final BuiltInROMs charRom;
		final BuiltInROMs kernalRom;
		
		Quick(Vic.Flavor vf, Oscillator o, Powerline p, BuiltInROMs kr) {
			vicFlavor = vf;
			oscillator = o;
			powerline = p;
			basicRom = BuiltInROMs.BASIC;
			kernalRom = kr;
			charRom = BuiltInROMs.CHAR;
		}
		Configuration getConfig(String name) {
			return new Configuration(name, vicFlavor, oscillator, powerline, basicRom, kernalRom, charRom);
		}
	}
	public final Vic.Flavor vicFlavor;
	public final Oscillator oscillator;
	public final Powerline powerline;
	public final String name;
	public final BuiltInROMs basicRom;
	public final BuiltInROMs charRom;
	public final BuiltInROMs kernalRom;
	
	private Configuration(String n, Vic.Flavor vf, Oscillator o, Powerline p,
			BuiltInROMs br, BuiltInROMs kr, BuiltInROMs cr) {
		name = n;
		vicFlavor = vf;
		oscillator = o;
		powerline = p;
		basicRom = br;
		kernalRom = kr;
		charRom = cr;
	}
	// TODO: more options, dialog?
}
