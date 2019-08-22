package us.malfeasant.admiral64.machine.cia;

import java.util.Calendar;

public class RealRTC extends RTC {
	@Override
	public int getTime() {
		Calendar now = Calendar.getInstance();
		int packed = now.get(Calendar.AM_PM) == Calendar.AM ? 0 : 0x800000;
		int hour = now.get(Calendar.HOUR);	// need to convert to bcd...
		// TODO more to come
		return packed;
	}
}
