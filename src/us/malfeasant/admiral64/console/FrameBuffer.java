package us.malfeasant.admiral64.console;

/**
 * Encapsulates the array of pixels- rather than Vic and Console needing to know about each other, this acts as an
 * intermediary, also tracks dimensions of both entire video field including blanking, and viewable area
 *  
 * @author Malfeasant
 */
public class FrameBuffer {
	private volatile int[] pixels;	// Since only the worker thread writes to this, shouldn't need full locking semantics
	// volatile gives the happens-before guarantee as long as the reference is rewritten before application thread reads it
	// 8 4-bit pixels are packed into a 32-bit int.
	
	final int cycles;
	final int lines;
	
	public FrameBuffer(int cycles, int lines) {
		this.cycles = cycles;
		this.lines = lines;
		pixels = new int[(cycles * lines)];
	}
	
	public void set(int cycle, int line, int packed) {
		assert cycle < cycles && line < lines : "Out of range.";
		pixels[cycle + line * cycles] = packed;
		pixels = pixels;	// Ensure the write is noticed by other threads	TODO: Experimental- verify it works as expected
	}
	public int get(int cycle, int line) {
		assert cycle < cycles && line < lines : "Out of range.";
		return pixels[cycle + line * cycles];
	}
}
