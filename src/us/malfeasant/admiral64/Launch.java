package us.malfeasant.admiral64;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

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
		Group root = new Group();
		Canvas canvas = new Canvas(800, 600);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);
		console.setScene(new Scene(root));
		console.show();
	}
}
