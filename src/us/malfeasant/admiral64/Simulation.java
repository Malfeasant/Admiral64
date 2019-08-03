package us.malfeasant.admiral64;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import us.malfeasant.admiral64.console.Console;
import us.malfeasant.admiral64.machine.Machine;
import us.malfeasant.admiral64.timing.TimingGenerator;
import us.malfeasant.admiral64.worker.WorkQueue;
import us.malfeasant.admiral64.worker.WorkQueue.WorkSender;
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
	private final WorkSender sender;
	private final Alert timingMonitor;
	
	public Simulation(Configuration conf) {
		config = conf;
		machine = new Machine(conf);
		WorkQueue queue = new WorkQueue();
		sender = queue.getSender();
		timingGen = new TimingGenerator(config.oscillator, config.powerline, machine, sender);
		worker = new WorkThread(queue.getReceiver(), timingGen);
		timingGen.cyclesProperty().get();	// and throw it away
		timingGen.ticksProperty().get();	// ditto
		// Otherwise they never get invalidated because they're never valid to begin with... joy.
		timingMonitor = new Alert(AlertType.INFORMATION);
		timingMonitor.initModality(Modality.NONE);
		timingMonitor.setTitle("Timing monitor");
		timingMonitor.setHeaderText("");
		timingMonitor.contentTextProperty().bind(Bindings.format(
				"%d cycles in %d seconds: %.9fMHz\n%d ticks in %d seconds: %.3fHz",
				timingGen.cyclesProperty(),
				timingGen.elapsedProperty().divide(1000000000),
				timingGen.cyclesProperty().multiply(1e3).divide(timingGen.elapsedProperty()),
				timingGen.ticksProperty(),
				timingGen.elapsedProperty().divide(1000000000),
				timingGen.ticksProperty().multiply(1e9).divide(timingGen.elapsedProperty())));
		
		console = new Console(conf.name);
		console.setBottom(timingGen.getButtons());
		console.setOnCloseRequest((event) -> {
			// TODO: Dialog- allow saving some state, cancel.  For now, just kill the sim.
			worker.die();
		});
		
		// would be much easier with binding, but it's read only
		console.addTimingMonitorMenuHandler((event) -> {
			timingGen.reset();	// start from a zero count
			timingMonitor.show();
		});
		
		machine.connectVideo(console);
//		timingGen.start();
		worker.start();
		console.start();
	}
}
