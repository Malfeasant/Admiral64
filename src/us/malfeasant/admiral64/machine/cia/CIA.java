package us.malfeasant.admiral64.machine.cia;

import us.malfeasant.admiral64.Configuration;

public class CIA {
	private final RTC rtc;
	
	public CIA(Configuration conf) {
		rtc = conf.rtcMode.factory.get();
	}
	
	/**
	 *	Tick the RTC
	 */
	public void tick() {
		// TODO: divide by 5 or 6 before passing to rtc
		rtc.tick();
	}
}
