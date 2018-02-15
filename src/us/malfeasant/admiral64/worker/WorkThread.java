package us.malfeasant.admiral64.worker;

import us.malfeasant.admiral64.machine.Machine;

public class WorkThread {
	private final Thread thread;
	private final Machine machine;
	private volatile boolean alive;
	
	public WorkThread(WorkQueue.WorkReceiver r, Machine m) {
		machine = m;
		thread = new Thread(() -> {
			alive = true;
			while (alive) {
				try {
					Request rq = r.receive();
					switch (rq.type) {
					case TICK:
						machine.tick();
						break;
					case CYCLES:
						for (int i=0; i<rq.count; i++) {
							machine.cycle();
						}
						break;
					}
					r.ack();
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
