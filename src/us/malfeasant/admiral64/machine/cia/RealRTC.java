package us.malfeasant.admiral64.machine.cia;

import java.time.LocalTime;
import java.time.temporal.ChronoField;

public class RealRTC extends RTC {
	@Override
	public int getTime() {	// packs current time into bcd bits: phhhhhmmmmmmmssssssstttt
		LocalTime now = LocalTime.now();
		int packed = now.get(ChronoField.AMPM_OF_DAY) << 23;	// am is 0, pm is 1
		int hour = now.get(ChronoField.CLOCK_HOUR_OF_AMPM);	// need to convert to bcd...
		packed |= (hour / 10) << 22;
		packed |= (hour % 10) << 18;
		int min = now.get(ChronoField.MINUTE_OF_HOUR);	// need to convert to bcd...
		packed |= (min / 10) << 15;
		packed |= (min % 10) << 11;
		int sec = now.get(ChronoField.SECOND_OF_MINUTE);	// need to convert to bcd...
		packed |= (sec / 10) << 8;
		packed |= (sec % 10) << 4;
		int ten = now.get(ChronoField.MILLI_OF_SECOND);
		packed |= (ten / 100);
		
		return packed;
	}
}
