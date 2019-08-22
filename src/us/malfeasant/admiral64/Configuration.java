package us.malfeasant.admiral64;

import us.malfeasant.admiral64.machine.bus.BuiltInROMs;
import us.malfeasant.admiral64.machine.cia.RTC;
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
	public final RTC.Mode rtcMode1;
	public final RTC.Mode rtcMode2;
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
		rtcMode1 = RTC.Mode.SIM;	// cia1 is connected to IRQ which is level sensitive
		rtcMode2 = RTC.Mode.REALTIME;	// cia2 gets NMI which is edge triggered
		// TODO: Test & decide whether to make configurable or just keep it like this- alarm time triggers interrupt-
		// level sensitive interrupt is more suited to fully simulated mode- since the two clocks run in sync,
		// sim will never miss a match, but realtime could- and since nmi is edge triggered, that can be faked and if
		// for example the sim is paused when the alarm time comes and goes, the 'edge' can be sensed when sim restarts.
		basicRom = br;
		kernalRom = kr;
		charRom = cr;
	}
	// TODO: more options, dialog?
}
