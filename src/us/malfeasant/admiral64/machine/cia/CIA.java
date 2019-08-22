package us.malfeasant.admiral64.machine.cia;

import us.malfeasant.admiral64.machine.cia.RTC.Mode;

public class CIA {
	private final RTC rtc;
	
	private int ticks;
	private int tickDiv = 6;	// There were no 2 different parts for EU/NA- all CIAs default to 60Hz power
	// must be changed in software for 50Hz.
	
	public CIA(Mode mode) {
		rtc = mode.factory.get();
	}
	
	/**
	 *	Tick the RTC
	 */
	public void tick() {
		ticks = (ticks + 1) % tickDiv;
		if (ticks == 0) rtc.tick();	// TODO: find out what happened if power freq divider was changed when tick was
		// already out of range- very small chance of it happpening, but possible...
	}
}
