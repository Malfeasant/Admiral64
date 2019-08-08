package us.malfeasant.admiral64.machine.vic;

public class Pixels {
	public final int column;
	public final int line;
//	private final byte[] pixels;	// TODO: test performance, compare to packed (8 4-bit pixels will pack into int)
	private final int pixels;	// doesn't seem to make much difference...
	public final Reader reader = new Reader();
	
	private Pixels(int x, int y, /*byte[]*/ int p) {
		column = x;
		line = y;
		pixels = p;
	}
	
	private static Pixels factory(int column, int line, /*byte[]*/ int pixels) {	// make it easy to cache these, for now just call constructor
		return new Pixels(column, line, pixels);
	}
	
	public static class Builder {
//		private final byte[] pixels = new byte[8];
		private int pixels;
		
		public void setColorAt(int position, int color) {
			assert color == (color & 0xf) : "Pixels: color out of range.";
			assert position == (position & 7) : "Pixels: position out of range.";
//			pixels[position] = (byte) color;
			pixels &= ~(0xf << 4*position);
			pixels |= color << 4*position;
		}
		
		public Pixels build(int column, int line) {
			return factory(column, line, pixels);
		}
	}
	
	public class Reader {
		public int getColorAt(int position) {
			assert position == (position & 7) : "Pixels: position out of range.";
//			return pixels[position];
			return (pixels >> 4*position) & 0xf;
		}
	}
}
