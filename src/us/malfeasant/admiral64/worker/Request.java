package us.malfeasant.admiral64.worker;

public class Request {
	enum Type {
		TICK, CYCLES;
	}
	final int count;
	final Type type;
	Request(Type t, int c) {	// don't want this to be constructed directly except by WorkQueue.Sender
		count = c;
		type = t;
	}
	static Request TICK = new Request(Type.TICK, 1);
}
