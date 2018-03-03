package us.malfeasant.admiral64.machine.vic;

public class Pixels {
	public final int column;
	public final int line;
	public final int pixels;	// 4-bits per pixel, 8 pixels pack into an int
	
	private Pixels(int x, int y, int p) {
		column = x;
		line = y;
		pixels = p;
	}
	
	public static Pixels factory(int x, int y, int p) {	// make it easy to cache these, for now just call constructor
		return new Pixels(x, y, p);
	}
}
