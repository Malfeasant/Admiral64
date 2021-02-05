package us.malfeasant.admiral64.machine.vic;

import us.malfeasant.admiral64.timing.CrystalConsumer;

/**
 * Manages BA/RDY and AEC signals
 * @author Malfeasant
 */
class BusControl implements CrystalConsumer {
	static final int CHAR_BA = 8;
	private int baFlags;
	// when any bit set, CPU stops read immediately, write within 3 cycles
	private boolean aec = true;	// signal from Vic that controls CPU bus- when false, cpu can't drive the bus
	private int count = 3;	// AEC needs to be delayed by 3 cycles
	
	BusControl() {
		// anything?
	}
	
	@Override
	public void posEdge() {
		aec = count != 0;	// if count has run out, aec will be false
	}

	@Override
	public void negEdge() {
		aec = false;	// always low when clock is low
		if (baFlags == 0) {
			count = 3;	// nothing needs the bus, reset the count
		} else if (count > 0) {	// something wants the bus, decrease counter if it's not already 0
			count--;
		}
	}
	
	/**
	 * Set (or clear) one of the flags which will stun the CPU
	 * @param which - 0-7 for respective sprites, 8 for char fetch
	 * @param b	what to set the flag to
	 */
	void setStun(int which, boolean b) {
		assert (which < 9) : "BA flag out of range.";
		int bit = 1 << which;
		baFlags = b ? (baFlags | bit) : (baFlags & ~bit);
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
