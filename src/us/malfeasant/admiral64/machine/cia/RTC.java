package us.malfeasant.admiral64.machine.cia;

import java.util.function.Supplier;

/**
 * The realtime clock of the CIAs.  Gets ticked in theory 10 times per second (divided down from powerline frequency by CIA)
 * @author Malfeasant
 */
public abstract class RTC {
	/**
	 * Represents the different ways the RTC can be handled
	 * Fully simulated- clock runs in sync w/ Oscillator, if paused/stepped RTC slows as well- time can be set from within simulation
	 * Partial realtime- time can be set in sim, but this is treated as offset- passage of time happens in realtime regardless of sim speed
	 * Full realtime- no setting time from within sim, read of clock reflects host local time no matter what
	 * (with the exception of latching)
	 * @author Malfeasant
	 */
	public enum Mode {
		SIM(() -> new SimRTC()),
		OFFSET(() -> null),	// TODO: obvious
		REALTIME(() -> new RealRTC());
		final Supplier<RTC> factory;
		Mode(Supplier<RTC> fact) {
			factory = fact;
		}
	}
	
	void tick() {}	// base method does nothing, only SimRTC overrides
	void setTime(int time) {}	// base method does nothing
	abstract int getTime();
}
