package us.malfeasant.admiral64;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import us.malfeasant.admiral64.timing.TimingGenerator;

public class Launch extends Application {

	@Override
	public void start(Stage primaryStage) {
		// The primaryStage will someday be used for a VirtualBox-like machine configurator / chooser.
		// For now, it stays hidden and opens a new stage to display a basic machine.
		primaryStage.setTitle("Placeholder");
		newMachine();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	static void newMachine() {	// TODO: accept config as arg, return something to use as key
		Stage console = new Stage();
		console.setTitle("Admiral 64");	// TODO: make title depend on machine config?
		Canvas canvas = new Canvas(800, 600);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		TimingGenerator tg = new TimingGenerator(gc);
		tg.start();
		VBox vbox = new VBox(canvas, tg.getButtons());
		
		BorderPane root = new BorderPane(vbox);
		console.setScene(new Scene(root));
		console.show();
	}
}
