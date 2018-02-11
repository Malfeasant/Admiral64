package us.malfeasant.admiral64;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import javafx.application.Platform;
import us.malfeasant.admiral64.console.Console;
import us.malfeasant.admiral64.console.Status;
import us.malfeasant.admiral64.timing.TimingGenerator;

/**
 *	Encompasses an entire simulation, including front end UI bits and back end work bits, and code to bridge them
 *	together.  
 */
public class Simulation {
	private final Configuration config;
	private final TimingGenerator timingGen;
	private final Console console;
	
	private final BlockingQueue<Object> workQueue;	// TODO: make more specific
	private volatile boolean alive;
	
	final Thread thread;
	
	public Simulation(Configuration conf) {
		config = conf;
		workQueue = new LinkedTransferQueue<>();
		timingGen = new TimingGenerator(config.oscillator, config.powerline, workQueue);
		Status status = new Status();
		status.cyclesProperty().bind(timingGen.cyclesProperty());
		status.ticksProperty().bind(timingGen.ticksProperty());
		status.elapsedProperty().bind(timingGen.elapsedProperty());
		timingGen.cyclesProperty().get();	// and throw it away
		timingGen.ticksProperty().get();	// ditto
		// Otherwise they never get invalidated because they're never valid to begin with... joy.
		thread = new Thread(() -> {
			alive = true;
			while (alive) {
				try {
					workQueue.take();	// TODO: do some real work
					Platform.runLater(() -> timingGen.workDone());
				} catch (InterruptedException e) {
					if (!alive) return;
				}
			}
		});
		thread.start();
		timingGen.start();
		
		console = new Console(conf.name, timingGen.getButtons(), status.getNode());
		console.setOnCloseRequest((event) -> {
			// TODO: Dialog- allow saving some state, cancel.  For now, just kill the sim.
			alive = false;
			thread.interrupt();
			System.out.println("Worker thread should be dead.");
		});
	}
}
