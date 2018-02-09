package us.malfeasant.admiral64.timing;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 *	generates the timing events that keep the machine running.  Minimum options should be single CPU cycle,  
 *	realtime, or uninhibited.  
 */
public class TimingGenerator extends AnimationTimer {
	RunMode mode = RunMode.STEP;
	// Most of these must be longs because AnimationTimer timestamp is in nanoseconds, which would overflow
	// an int in 2 seconds.  Even cpu cycles would overflow in 34 minutes.  Ticks would go over a year, but
	// running at high speed could conceivably reach that in sim time...
	long last;	// last frame timestamp
	long elapsed;	// total time since arbitrary point
	long interval;	// current frame's duration
	long cyclesDone;	// total cycles run since arbitrary point
	long cycleRem;	// leftover time to run in next interval (only used for realtime)
	final int cyclesPerTick;	// integer part of how many cpu cycles per RTC tick
	long ticksDone;	//	Total number of power cycles (used to tick CIAs' RTC)
	int tickRem;	// remainder from above calculation
	
	private final HBox buttons = new HBox();
	final Oscillator osc = Oscillator.NTSC;	// TODO: make this configurable
	final Powerline pow = Powerline.NA;	// TODO: same
	final GraphicsContext context;
	
	@Override
	public void handle(long now) {
		if (last > 0) {	// not first run
			interval = now - last;
			elapsed += interval;
			mode.timerFired(this);
			// TODO: Render worker thread results to screen
			// TODO: if realtime, compute # of cycles to request from worker thread
		}	// skip first run
		last = now;
	}
	
	public TimingGenerator(GraphicsContext gc) {
		cyclesPerTick = osc.cycles / (osc.seconds * pow.cycles);	// Integer is accurate enough
		for (RunMode m : RunMode.values()) {
			buttons.getChildren().add(m.makeButton(this));
		}
		buttons.setAlignment(Pos.CENTER);
		context = gc;
	}
	
	void runFor(int cycles) {
		ticksDone += (cycles + tickRem) / cyclesPerTick;
		tickRem = (cycles + tickRem) % cyclesPerTick;
		// TODO: Send message to worker thread
		cyclesDone += cycles;
		Platform.runLater(() -> mode.workDone(this));	// simulate a response from worker thread
		
		context.setFill(Color.grayRgb(0xf4));
		context.fillRect(0, 0, 800, 600);
		context.setFill(Color.BLACK);
		context.fillText(String.format("%9d cycles\t%5.3f milliseconds\t%.9fMHz",
				cycles, interval / 1e6, cycles * 1e3 / interval), 20, 20);
		context.fillText(String.format("Long term: %d cycles\t%.3f seconds\t%.9fMHz",
				cyclesDone, elapsed / 1e9, cyclesDone * 1e3 / elapsed), 40, 40);
		context.fillText(String.format("Powerline: %d cycles\t%.3f seconds\t%.9fHz",
				ticksDone, elapsed / 1e9, ticksDone * 1e9 / elapsed), 20, 60);
	}
	public HBox getButtons() {
		return buttons;
	}
}
