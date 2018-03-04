package us.malfeasant.admiral64.console;

import java.util.function.Consumer;

import javafx.animation.AnimationTimer;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import us.malfeasant.admiral64.machine.vic.Pixels;

public class Console extends AnimationTimer implements Consumer<Pixels> {
	private final Stage window;
	private final Canvas canvas;
	private final GraphicsContext context;
	private final WritableImage image;
	private final PixelWriter pixelWriter;
	
	private static final Color[] palette = {
		Color.BLACK, Color.WHITE, 
		Color.web("813338"),	// Red
		Color.web("75cec8"),	// Cyan
		Color.web("8e3c97"),	// Purple
		Color.web("56ac4d"),	// Green
		Color.web("2e2c9b"),	// Blue
		Color.web("edf171"),	// Yellow
		Color.web("8e5029"),	// Orange
		Color.web("553800"),	// Brown
		Color.web("c46c71"),	// Lt. Red
		Color.web("4a4a4a"),	// Dark Gray
		Color.web("7b7b7b"),	// Med. Gray
		Color.web("a9ff9f"),	// Lt. Green
		Color.web("706deb"),	// Lt. Blue
		Color.web("b2b2b2") 	// Lt. Gray
	};
	
	public Console(String title, Node buttons, Node status) {
		window = new Stage();
		window.setTitle(title);
		
		canvas = new Canvas();
		context = canvas.getGraphicsContext2D();
		
		image = new WritableImage(520, 312);	// TODO: match Vic dimensions
		pixelWriter = image.getPixelWriter();
		
		BorderPane root = new BorderPane(canvas);
		root.setBottom(buttons);
		window.setScene(new Scene(root));
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				window.getWidth(); window.getHeight();	// make bindings valid
				double width = Math.min(window.getWidth(),
						(window.getHeight() * 1.33333) - buttons.getLayoutBounds().getHeight());
				double height = Math.min(window.getHeight() - buttons.getLayoutBounds().getHeight(),
						window.getWidth() * 0.75);
				canvas.setWidth(width);
				canvas.setHeight(height);
//				System.out.println("Setting width: " + Math.round(width) + "\theight: " + Math.round(height));
			}
		};
		window.widthProperty().addListener(listener);
		window.heightProperty().addListener(listener);
		window.show();
	}
	
	public void setOnCloseRequest(EventHandler<WindowEvent> e) {
		window.setOnCloseRequest(e);
	}
	
	@Override
	public void handle(long now) {
		synchronized (image) {
			context.drawImage(image,
					0, 41, 376, 220,
					0, 0, canvas.getWidth(), canvas.getHeight());
		}
	}
	
	/**
	 *	This will be called from the worker thread
	 *	TODO: measure performance, compare with sticking stuff into a queue then dumping it out all at once
	 *	in the handle() method
	 */
	@Override
	public void accept(Pixels bar) {
		synchronized (image) {
			for (int x=0; x<8; x++) {
				pixelWriter.setColor(bar.column * 8 + x, bar.line, palette[(bar.pixels >> (7 - x) * 4) & 0xf]);
			}
		}
	}
}
