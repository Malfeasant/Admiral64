package us.malfeasant.admiral64;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import us.malfeasant.admiral64.console.Console;
import us.malfeasant.admiral64.machine.Machine;
import us.malfeasant.admiral64.timing.TimingGenerator;
import us.malfeasant.admiral64.timing.TimingMonitor;
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
			new TimingMonitor(timingGen);
		});
		
		worker.start();
		console.start();
		window.show();
	}
}
