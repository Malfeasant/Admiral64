package us.malfeasant.admiral64.worker;

public class WorkThread {
	private final Thread thread;
	private volatile boolean alive;
	
	public WorkThread(WorkQueue.WorkReceiver r) {
		thread = new Thread(() -> {
			alive = true;
			while (alive) {
				try {
					Request rq = r.receive();
					// TODO: do some real work
					if (rq.type.equals(Request.Type.DONE)) r.ack();
				} catch (InterruptedException e) {
					if (!alive) return;
				}
			}
		});
	}
	
	public void start() {
		thread.start();
	}
	public void die() {
		alive = false;
		thread.interrupt();
	}
}
