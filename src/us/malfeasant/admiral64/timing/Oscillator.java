package us.malfeasant.admiral64.timing;

/**	These are somewhat magic numbers representing a number of seconds per number of CPU cycles (or vice versa).
 *	Done this way since otherwise they are ugly repeating decimals- this allows storing as ints.
 */
public enum Oscillator {
	NTSC(11, 11250000), PAL(18, 17734475);
	final int seconds;
	final int cycles;
	
	private Oscillator(int seconds, int cycles) {
		this.seconds = seconds;
		this.cycles = cycles;
	}
}
