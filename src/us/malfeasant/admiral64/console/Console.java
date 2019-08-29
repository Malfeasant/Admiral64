package us.malfeasant.admiral64.console;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Console extends AnimationTimer {
//	private final Stage window;	// moving to Simulation
	private final ImageView canvas;
	private final WritableImage image;
	private final PixelWriter pixelWriter;
//	private final BorderPane root;	// moving to Simulation
//	private final MenuItem showTiming;
	private final FrameBuffer pixelBuffer;
	
	private static final Color[] palette = {	// TODO: make this configurable
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
	
	public Console(FrameBuffer fb) {
		pixelBuffer = fb;
		
		image = new WritableImage(520, 312);	// TODO: match Vic dimensions- maybe defer creation until pixel buffer is attached?
		pixelWriter = image.getPixelWriter();
		canvas = new ImageViewWrapper(image);
		canvas.setViewport(new Rectangle2D(0, 41, 376, 220));
	}
	
	public Node getNode() {
		return canvas;
	}
	
	@Override
	public void handle(long now) {
		// TODO: only update visible portion
		// TODO: keep track of how much of buffer has changed, only transfer what is needed
		for (int line = 0; line < pixelBuffer.lines; line++) {
			for (int cycle = 0; cycle < pixelBuffer.cycles; cycle++) {
				int packed = pixelBuffer.get(cycle, line);
				for (int pos = 0; pos < 8; pos++) {
					pixelWriter.setColor(cycle * 8 + pos, line, palette[(packed >> (4 * (7 - pos)) & 0xf)]);
				}
			}
		}
	}
}
