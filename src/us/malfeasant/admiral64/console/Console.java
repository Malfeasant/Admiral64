package us.malfeasant.admiral64.console;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Console extends AnimationTimer {
	private final Stage window;
	private final GraphicsContext context;
	private final WritableImage image;
	
	public Console(String title, Node buttons, Node status) {
		window = new Stage();
		window.setTitle(title);
		
		Canvas canvas = new Canvas(800, 600);
		context = canvas.getGraphicsContext2D();
		VBox vbox = new VBox(canvas, buttons);
		
		image = new WritableImage(520, 312);	// TODO: match Vic dimensions
		
		BorderPane root = new BorderPane(vbox);
		root.setBottom(status);
		window.setScene(new Scene(root));
		window.show();
	}
	
	public void setOnCloseRequest(EventHandler<WindowEvent> e) {
		window.setOnCloseRequest(e);
	}
	
	public PixelWriter getWriter() {
		return image.getPixelWriter();
	}

	@Override
	public void handle(long now) {
		context.drawImage(image, 0, 0);
	}
}
