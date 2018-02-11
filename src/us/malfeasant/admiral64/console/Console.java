package us.malfeasant.admiral64.console;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Console {
	Stage window;
	
	public Console(String title, Node buttons, Node status) {
		window = new Stage();
		window.setTitle(title);
		
		Canvas canvas = new Canvas(800, 600);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		VBox vbox = new VBox(canvas, buttons);
		
		BorderPane root = new BorderPane(vbox);
		root.setBottom(status);
		window.setScene(new Scene(root));
		window.show();
	}
	
	public void setOnCloseRequest(EventHandler<WindowEvent> e) {
		window.setOnCloseRequest(e);
	}
}
