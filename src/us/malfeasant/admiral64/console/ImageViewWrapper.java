package us.malfeasant.admiral64.console;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewWrapper extends ImageView {
	public ImageViewWrapper(Image i) {
		super(i);
	}
	
	@Override
	public double minWidth(double height) {
		return height < 0 ? 40 : height * 4 / 3;
	}
	
	@Override
	public double prefWidth(double height) {
		return height < 0 ? 400 : height * 4 / 3;
	}
	
	@Override
	public double maxWidth(double height) {
		return Double.MAX_VALUE;
	}
	
	@Override
	public double minHeight(double width) {
		return width < 0 ? 30 : width * 3 / 4;
	}
	
	@Override
	public double prefHeight(double width) {
		return width < 0 ? 300 : width * 3 / 4;
	}
	
	@Override
	public double maxHeight(double width) {
		return Double.MAX_VALUE;
	}
	
	@Override
	public boolean isResizable() {
		return true;
	}
	
	@Override
	public void resize(double width, double height) {
		setFitWidth(Math.min(width, height * 4 / 3));
		setFitHeight(Math.min(height, width * 3 / 4));
	}
}
