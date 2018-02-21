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
		Color.hsb(360*4/16.0, 1, 10/32.0),	// Red
		Color.hsb(360*12/16.0, 1, 20/32.0),	// Cyan
		Color.hsb(360*2/16.0, 1, 12/32.0),	// Purple
		Color.hsb(360*10/16.0, 1, 16/32.0),	// Green
		Color.hsb(360*15/16.0, 1, 8/32.0),	// Blue
		Color.hsb(360*7/16.0, 1, 24/32.0),	// Yellow
		Color.hsb(360*5/16.0, 1, 12/32.0),	// Orange
		Color.hsb(360*6/16.0, 1, 8/32.0),	// Brown
		Color.hsb(360*4/16.0, 1, 16/32.0),	// Lt. Red
		Color.gray(10/32.0),	// Dark Gray
		Color.gray(15/32.0),	// Med. Gray
		Color.hsb(360*10/16.0, 1, 24/32.0),	// Lt. Green
		Color.hsb(360*15/16.0, 1, 15/32.0),	// Lt. Blue
		Color.gray(20/32.0) 	// Lt. Gray
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
					pixelWriter.setColor(pixel, line, palette[buffer[y][x]]);
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
