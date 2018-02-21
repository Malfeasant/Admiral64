package us.malfeasant.admiral64.worker;

import javafx.application.Platform;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/**
 *	Buffers pixels from worker thread, makes available to UI thread.
 */
public class VideoOut {
	private PixelWriter writer;
	private static Color[] palette = {	// TODO: make this configurable
		Color.BLACK, Color.WHITE, 
		Color.hsb(4/16.0, 1, 10/32.0),	// Red
		Color.hsb(12/16.0, 1, 20/32.0),	// Cyan
		Color.hsb(2/16.0, 1, 12/32.0),	// Purple
		Color.hsb(10/16.0, 1, 16/32.0),	// Green
		Color.hsb(15/16.0, 1, 8/32.0),	// Blue
		Color.hsb(7/16.0, 1, 24/32.0),	// Yellow
		Color.hsb(5/16.0, 1, 12/32.0),	// Orange
		Color.hsb(6/16.0, 1, 8/32.0),	// Brown
		Color.hsb(4/16.0, 1, 16/32.0),	// Lt. Red
		Color.gray(10/32.0),	// Dark Gray
		Color.gray(15/32.0),	// Med. Gray
		Color.hsb(10/16.0, 1, 24/32.0),	// Lt. Green
		Color.hsb(15/16.0, 1, 15/32.0),	// Lt. Blue
		Color.gray(20/32.0) 	// Lt. Gray
	};
	public void send(int x, int y, int c) {
		writer.setColor(x, y, palette[c]);
	}
	
	public void setWriter(PixelWriter w) {
		writer = w;
	}
}
