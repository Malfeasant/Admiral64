package us.malfeasant.admiral64;

import javafx.application.Application;
import javafx.stage.Stage;

public class Launch extends Application {

	@Override
	public void start(Stage primaryStage) {
		// The primaryStage will someday be used for a VirtualBox-like machine configurator / chooser.
		// For now, it stays hidden and opens a new stage to display a basic machine.
		primaryStage.setTitle("Placeholder");
		new Simulation(Configuration.Quick.NEW_NTSC.getConfig("Admiral64"));	// TODO: accept config as arg
	}

	public static void main(String[] args) {
		launch(args);
	}
}
