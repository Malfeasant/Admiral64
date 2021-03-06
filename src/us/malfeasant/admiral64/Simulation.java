package us.malfeasant.admiral64;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
	private final Stage window;
	private final BorderPane root;
	private final Configuration config;
	private final TimingGenerator timingGen;
	private final Console console;
	private final Machine machine;
	private final WorkThread worker;
	private final WorkSender sender;
	private final Alert timingMonitor;
	
	public Simulation(Configuration conf) {
		config = conf;
		
		window = new Stage();
		window.setTitle(conf.name);
		
		root = new BorderPane();
		window.setScene(new Scene(root));
		
		machine = new Machine(conf);
		WorkQueue queue = new WorkQueue();
		sender = queue.getSender();
		timingGen = new TimingGenerator(config.oscillator, config.powerline, sender);
		machine.connectTiming(timingGen);
		
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
				timingGen.elapsedProperty().divide(1000),
				timingGen.cyclesProperty().divide(timingGen.elapsedProperty().multiply(1e3)),
				timingGen.ticksProperty(),
				timingGen.elapsedProperty().divide(1000),
				timingGen.ticksProperty().multiply(1e3).divide(timingGen.elapsedProperty())));
		
		console = new Console(machine.getFrameBuffer());
		
		root.setCenter(console.getNode());
		root.setBottom(timingGen.getButtons());
		
		window.setOnCloseRequest((event) -> {
			// TODO: Dialog- allow saving some state, cancel.  For now, just kill the sim.
			worker.die();
		});
		
		MenuItem showTiming = new MenuItem("Show timing...");
		Menu debugMenu = new Menu("Debug");
		debugMenu.getItems().add(showTiming);
		MenuBar bar = new MenuBar();
		bar.getMenus().add(debugMenu);
		root.setTop(bar);
		
		// would be much easier with binding, but it's read only
		showTiming.addEventHandler(ActionEvent.ACTION, (event) -> {
			timingGen.resetDebugCounters();	// start from a zero count
			timingMonitor.show();
		});
		
		worker.start();
		console.start();
		window.show();
	}
}
