package us.malfeasant.admiral64.worker;

import java.util.Optional;

import us.malfeasant.admiral64.timing.RunMode;
import us.malfeasant.admiral64.timing.TimingGenerator;

public class WorkThread {
	private final Thread thread;
	private final TimingGenerator timingGen;
	private RunMode mode;
	private volatile boolean alive;
	
	public WorkThread(WorkQueue.WorkReceiver r, TimingGenerator t) {
		timingGen = t;
		mode = RunMode.getDefault();
		thread = new Thread(() -> {
			alive = true;
			while (alive) {
				Optional<RunMode> optMode = r.receive();
				optMode.ifPresent(newMode -> mode = newMode);
				if (mode == RunMode.STEP) {	// Sim is already paused
					try {
						RunMode gotMode = r.receiveWait();	// wait for a new RunMode to arrive
						if (gotMode == RunMode.STEP) {	// user just clicked step
							timingGen.run(gotMode);
						} else {	// User clicked another mode button
							mode = gotMode;	// set the new mode, will run it on the next iteration
							// Also, this is still inside the try because if interrupted, there's no mode to set
						}
					} catch (InterruptedException e) {
						// TODO: Anything? if interruption was intentional, alive will be false and we'll quit soon enough.
						// Otherwise, just fall out of the loop and run it again
						continue;
					}
				} else {
					timingGen.run(mode);
				}
			}
		});
	}
	
	public void start() {
		thread.start();
	}
	public void die() {
		alive = false;
		System.out.println("Waiting for worker thread...");
		try {
			thread.join(1000);	// give it a second to come quietly
			System.out.println("Got it.");
		} catch (InterruptedException e) {
			System.out.println("Worker thread is not paying attention, need to interrupt.");
			thread.interrupt();
		}
	}
}
