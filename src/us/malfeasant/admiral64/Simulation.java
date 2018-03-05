package us.malfeasant.admiral64;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import us.malfeasant.admiral64.console.Console;
import us.malfeasant.admiral64.machine.Machine;
import us.malfeasant.admiral64.timing.TimingGenerator;
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
	
	public Simulation(Configuration conf) {
		config = conf;
		machine = new Machine(conf);
		WorkQueue queue = new WorkQueue(() -> ack());
		worker = new WorkThread(queue.getReceiver(), machine);
		timingGen = new TimingGenerator(config.oscillator, config.powerline, queue.getSender());
		timingGen.cyclesProperty().get();	// and throw it away
		timingGen.ticksProperty().get();	// ditto
		// Otherwise they never get invalidated because they're never valid to begin with... joy.
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initModality(Modality.NONE);
		alert.setTitle("Clock monitor");
		alert.setHeaderText("");
		alert.contentTextProperty().bind(Bindings.format("%d cycles in %d seconds: %.9fMHz",
				timingGen.cyclesProperty(),
				timingGen.elapsedProperty().divide(1000000000),
				timingGen.cyclesProperty().multiply(1e3).divide(timingGen.elapsedProperty())));
		alert.show();
		
		console = new Console(conf.name, timingGen.getButtons());
		console.setOnCloseRequest((event) -> {
			// TODO: Dialog- allow saving some state, cancel.  For now, just kill the sim.
			worker.die();
		});
		machine.connectVideo(console);
		timingGen.start();
		worker.start();
		console.start();
	}
	private void ack() {
		timingGen.workDone();
	}
}
