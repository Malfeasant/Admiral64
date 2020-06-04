package us.malfeasant.admiral64.machine.vic;

/**
 * Manages BA/RDY and AEC signals
 * @author Malfeasant
 */
class BusControl {
	static final int CHAR_BA = 8;
	private int baFlags;
	// when any bit set, CPU stops read immediately, write within 3 cycles
	private boolean aec = true;	// signal from Vic that controls CPU bus- when false, cpu can't drive the bus
	private int count;	// AEC needs to be delayed by 3 cycles
	
	BusControl() {
		// anything?
	}
	
	void cycle() {
		if (baFlags == 0) {
			aec = true;
			count = 0;
		} else {
			if (aec) count++;
			if (count >= 3) aec = false;
		}
	}
	
	/**
	 * Set (or clear) one of the flags which will stun the CPU
	 * @param which - 0-7 for respective sprites, 8 for char fetch
	 * @param b	what to set the flag to
	 */
	void setStun(int which, boolean b) {
		assert (which < 9) : "BA flag out of range.";
		if (b) {
			baFlags |= 1 << which;	// set the given flag
		} else {
			baFlags &= ~(1 << which);	// clear the given flag
		}
	}
	/**
	 * Gets the combined result of all flags- if no stun flag is set, means bus is available
	 * @return if ba is asserted- true: cpu is allowed to run- false: read will halt, write will complete within 3 cycles
	 */
	boolean getBA() {
		return baFlags == 0;
	}
	/**
	 * Gets the (delayed) result of all flags
	 * @return if aec is asserted, cpu can drive the bus
	 */
	boolean getAEC() {
		return aec;
	}
}
