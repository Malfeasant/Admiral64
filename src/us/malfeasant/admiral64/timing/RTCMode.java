package us.malfeasant.admiral64.timing;

/**
 * Represents the different ways the RTC can be handled- note, there are actually two RTCs, one in each CIA chip.  Maybe let
 * each have its mode set separately?
 * Fully simulated- clock runs in sync w/ Oscillator, if paused/stepped RTC slows as well- time can be set from within simulation
 * Partial realtime- time can be set in sim, but this is treated as offset- passage of time happens in realtime regardless of sim speed
 * Full realtime- no setting time from within sim, read of clock reflects host local time no matter what
 * (with the exception of latching)
 * @author Malfeasant
 */
public enum RTCMode {
	SIM, OFFSET, REALTIME;
}
