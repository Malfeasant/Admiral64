package us.malfeasant.admiral64;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import us.malfeasant.admiral64.timing.Oscillator;
import us.malfeasant.admiral64.timing.Powerline;
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
		TimingGenerator tg = new TimingGenerator(Oscillator.NTSC, Powerline.NA);
		tg.start();
		VBox vbox = new VBox(canvas, tg.getButtons());
		
		Text cycles = new Text();
		Text ticks = new Text();
		tg.cyclesProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (tg.getElapsed() > 0) // Avoid ugly divide by zero
					cycles.setText(String.format("%d cycles in %d seconds: %.9fMHz",
							tg.getCycles(), tg.getElapsed() / 1000000000, tg.getCycles() * 1e3 / tg.getElapsed() ));
			}
		});
		tg.ticksProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (tg.getElapsed() > 0) 
					ticks.setText(String.format("%d ticks in %d seconds: %.3fHz",
							tg.getTicks(), tg.getElapsed() / 1000000000, tg.getTicks() * 1e9 / tg.getElapsed() ));
			}
		});
		VBox status = new VBox(cycles, ticks);
		
		BorderPane root = new BorderPane(vbox);
		root.setBottom(status);
		console.setScene(new Scene(root));
		console.show();
	}
}
