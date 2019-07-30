package us.malfeasant.admiral64.worker;

import us.malfeasant.admiral64.machine.Machine;
import us.malfeasant.admiral64.timing.RunMode;

public class WorkThread {
	private final Thread thread;
	private final Machine machine;
	private RunMode mode;
	private volatile boolean alive;
	
	public WorkThread(WorkQueue.WorkReceiver r, Machine m) {
		machine = m;
		mode = RunMode.getDefault();
		thread = new Thread(() -> {
			alive = true;
			while (alive) {
				switch (mode) {
				case STEP:
					try {
						RunMode gotMode = r.receiveWait();	// wait for a new RunMode to arrive
						if (gotMode == RunMode.STEP) {	// In the case means we're already paused, in the if means user clicked step 
							machine.tick();	// Can't have this in main body of switch, otherwise it'll run every iteration
							// TODO: figure out whether/when to tick RTC
						} else {
							mode = gotMode;	// User clicked a different mode button, set it and end this iteration
						}
					} catch (InterruptedException e) {
						// TODO: Anything? if interruption was intentional, alive will be false and we'll quit soon enough.
						// Otherwise, just fall out of the loop and run it again
					}
					break;
				case REAL:
					// TODO: figure out number of cycles to run, when to tick RTC, when done, sleep for some amount of time
					break;
				case FAST:
					machine.cycle(0x4000);	// Run a burst, don't wait when done.
					// TODO: figure out whether/when to tick RTC
					break;
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
