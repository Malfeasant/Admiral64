package us.malfeasant.admiral64.worker;

public class Request {
	public enum Type {
		TICK, CYCLES, DONE;
	}
	final int count;
	final Type type;
	Request(Type t, int c) {	// don't want this to be constructed directly except by WorkQueue.Sender
		count = c;
		type = t;
	}
	static Request TICK = new Request(Type.TICK, 1);
	static Request DONE = new Request(Type.DONE, -1);
}
