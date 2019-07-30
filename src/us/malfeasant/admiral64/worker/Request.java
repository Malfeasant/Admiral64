package us.malfeasant.admiral64.worker;

public class Request {
	enum Type {
		IDLE, RTC, OSC;
	}
	final int count;
	final Type type;
	Request(Type t, int c) {	// don't want this to be constructed directly except by WorkQueue.Sender
		count = c;
		type = t;
	}
	static final Request IDLE = new Request(Type.IDLE, 0);
	static final Request RTCTICK = new Request(Type.RTC, 1);
	static final Request OSCBURST = new Request(Type.OSC, 0x4000);	// At realtime, around 60 bursts per second
}
