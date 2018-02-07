package us.malfeasant.admiral64.timing;

/**
 *	Specifies how the RTCs within the CIA chips notice the passage of time-
 *	PROPORTIONAL locks the counters to some fraction of Oscillator cycles, so stops when the machine is paused etc.
 *	CONSTANT updates the counters in realtime, so time passes even when paused.  Does not affect setting time.
 *	PASSTHROUGH bypasses most of the RTC logic (including setting current time) and translates local time into the
 *	RTCs registers, regardless of the machine speed, pauses, etc.
 */
public enum ClockMode {
	PROPORTIONAL, CONSTANT, PASSTHROUGH;
}
