package us.malfeasant.admiral64;

import us.malfeasant.admiral64.console.Console;
import us.malfeasant.admiral64.console.Status;
import us.malfeasant.admiral64.machine.Machine;
import us.malfeasant.admiral64.timing.TimingGenerator;
import us.malfeasant.admiral64.worker.VideoOut;
import us.malfeasant.admiral64.worker.WorkQueue;
import us.malfeasant.admiral64.worker.WorkThread;

/**
 *	Encompasses an entire simulation, including front end UI bits and back end work bits, and code to bridge them
 *	together.  
 */
public class Simulation {
	private final Configuration config;
	private final TimingGenerator timingGen;
	private final Console console;
	private final Machine machine;
	private final WorkThread worker;
	private final Status status;	// have to keep this or this object gets garbage collected, which causes its bindings to break.
	
	public Simulation(Configuration conf) {
		config = conf;
		VideoOut vOut = new VideoOut();
		machine = new Machine(conf, vOut);
		WorkQueue queue = new WorkQueue(() -> ack());
		worker = new WorkThread(queue.getReceiver(), machine);
		timingGen = new TimingGenerator(config.oscillator, config.powerline, queue.getSender());
		status = new Status();
		status.cyclesProperty().bind(timingGen.cyclesProperty());
		status.ticksProperty().bind(timingGen.ticksProperty());
		status.elapsedProperty().bind(timingGen.elapsedProperty());
		timingGen.cyclesProperty().get();	// and throw it away
		timingGen.ticksProperty().get();	// ditto
		// Otherwise they never get invalidated because they're never valid to begin with... joy.
		
		console = new Console(conf.name, timingGen.getButtons(), status.getNode());
		console.setOnCloseRequest((event) -> {
			// TODO: Dialog- allow saving some state, cancel.  For now, just kill the sim.
			worker.die();
		});
		vOut.setWriter(console.getWriter());
		
		timingGen.start();
		worker.start();
	}
	private void ack() {
		timingGen.workDone();
	}
}
