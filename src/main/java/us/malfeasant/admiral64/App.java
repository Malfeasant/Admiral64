package us.malfeasant.admiral64;

import org.tinylog.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Logger.debug("Building window...");

        var label = new Label("Running...");
        var pane = new BorderPane(label);
        stage.setScene(new Scene(pane));
        stage.show();
    }

	public static void main(String[] args) {
		Logger.debug("Starting JavaFX App...");
		launch(args);
	}
}
