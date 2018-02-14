package us.malfeasant.admiral64.worker;

public class Request {
	enum Type {
		TICK, CYCLES;
	}
	final int count;
	final Type type;
	private Request(Type t, int c) {	// don't want this to be constructed directly
		count = c;
		type = t;
	}
	private static Request TICK = new Request(Type.TICK, 1);
	
	public static Request requestTick() {
		return TICK;
	}
	public static Request requestCycles(int cycles) {
		return new Request(Type.CYCLES, cycles);
		// TODO: measure performance, may make sense to maintain a cache of common counts
	}
}
