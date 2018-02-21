package us.malfeasant.admiral64.console;

import java.util.function.Consumer;

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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import us.malfeasant.admiral64.machine.vic.VideoEvent;

public class Console extends AnimationTimer implements Consumer<VideoEvent> {
	private final Stage window;
	private final GraphicsContext context;
	private final WritableImage image;
	private final PixelWriter pixelWriter;
	
	private final byte[][] buffer;
	private int x;
	private int y;
	
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
		
		Canvas canvas = new Canvas(800, 600);
		context = canvas.getGraphicsContext2D();
		VBox vbox = new VBox(canvas, buttons);
		
		buffer = new byte[312][520];
		image = new WritableImage(520, 312);	// TODO: match Vic dimensions
		pixelWriter = image.getPixelWriter();
		
		BorderPane root = new BorderPane(vbox);
		root.setBottom(status);
		window.setScene(new Scene(root));
		window.show();
	}
	
	public void setOnCloseRequest(EventHandler<WindowEvent> e) {
		window.setOnCloseRequest(e);
	}
	
	@Override
	public void handle(long now) {
		synchronized (buffer) {
			for (int line = 0; line < buffer.length; line++) {
				for (int pixel = 0; pixel < buffer[line].length; pixel++) {
					pixelWriter.setColor(pixel, line, palette[buffer[line][pixel]]);
				}
			}
		}
		context.drawImage(image, 0, 0);
	}
	
	/**
	 *	This will be called from the worker thread
	 */
	@Override
	public void accept(VideoEvent t) {
		synchronized (buffer) {
			switch (t) {
			case HSYNC:
				x = 0;
				y++;
				break;
			case VSYNC:
				y = 0;
				break;
			default:
				buffer[y][x] = (byte) t.ordinal();
				x++;
			}
		}
	}
}
