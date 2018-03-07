package us.malfeasant.admiral64.console;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import us.malfeasant.admiral64.machine.vic.Pixels;

public class Console extends AnimationTimer implements Consumer<Pixels> {
	private final Stage window;
	private final ImageView canvas;
	//private final GraphicsContext context;
	private final WritableImage image;
	private final PixelWriter pixelWriter;
	private final BorderPane root;
	private final MenuItem showTiming;
	private final BlockingQueue<Pixels> queue = new LinkedBlockingQueue<>();
	
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
	
	public Console(String title) {
		window = new Stage();
		window.setTitle(title);
		
		image = new WritableImage(520, 312);	// TODO: match Vic dimensions
		pixelWriter = image.getPixelWriter();
		canvas = new ImageView(image);
		canvas.setViewport(new Rectangle2D(0, 41, 384, 220));
		canvas.fitWidthProperty().bind(window.widthProperty());
		canvas.fitHeightProperty().bind(window.widthProperty().multiply(.75));
		
		root = new BorderPane(canvas);
		window.setScene(new Scene(root));
		
		showTiming = new MenuItem("Show timing...");
		Menu debugMenu = new Menu("Debug");
		debugMenu.getItems().add(showTiming);
		MenuBar bar = new MenuBar();
		bar.getMenus().add(debugMenu);
		root.setTop(bar);
		
		window.show();
	}
	
	public void addTimingMonitorMenuHandler(EventHandler<ActionEvent> eventHandler) {
		showTiming.addEventHandler(ActionEvent.ACTION, eventHandler);
	}
	
	public void setBottom(Node n) {
		root.setBottom(n);
	}
	public void setOnCloseRequest(EventHandler<WindowEvent> e) {
		window.setOnCloseRequest(e);
	}
	
	@Override
	public void handle(long now) {
		List<Pixels> pixelList = new ArrayList<>();
		queue.drainTo(pixelList);
		for (Pixels p : pixelList) {
			for (int x=0; x<8; x++) {
				pixelWriter.setColor(p.column * 8 + x, p.line, palette[(p.reader.getColorAt(x))]);
			}
		}
	}
	
	/**
	 *	This will be called from the worker thread
	 */
	@Override
	public void accept(Pixels bar) {
		boolean success = queue.offer(bar);	// queue is unbounded, so shouldn't ever fail
		assert success : "Pixel queue apparently full.";
	}
}
