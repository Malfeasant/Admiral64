package us.malfeasant.admiral64.timing;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

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
	final ReadOnlyLongWrapper elapsed = new ReadOnlyLongWrapper();	// total time since arbitrary point
	long interval;	// current frame's duration
	final ReadOnlyLongWrapper cyclesDone = new ReadOnlyLongWrapper();	// total cycles run since arbitrary point
	long cycleRem;	// leftover time to run in next interval (only used for realtime)
	final int cyclesPerTick;	// integer part of how many cpu cycles per RTC tick
	final ReadOnlyLongWrapper ticksDone = new ReadOnlyLongWrapper();	//	Total number of power cycles (used to tick CIAs' RTC)
	int tickRem;	// remainder from above calculation
	
	private final HBox buttons = new HBox();
	final Oscillator osc;
	final Powerline pow;
	
	@Override
	public void handle(long now) {
		if (last > 0) {	// not first run
			interval = now - last;
			elapsed.set(elapsed.get() + interval);
			mode.timerFired(this);
			// TODO: Render worker thread results to screen
			// TODO: if realtime, compute # of cycles to request from worker thread
		}	// skip first run
		last = now;
	}
	
	public TimingGenerator(Oscillator o, Powerline p) {
		osc = o;
		pow = p;
		cyclesPerTick = osc.cycles / (osc.seconds * pow.cycles);	// Integer is accurate enough
		for (RunMode m : RunMode.values()) {
			buttons.getChildren().add(m.makeButton(this));
		}
		buttons.setAlignment(Pos.CENTER);
	}
	
	void runFor(int cycles) {
		ticksDone.set(ticksDone.get() + (cycles + tickRem) / cyclesPerTick);
		tickRem = (cycles + tickRem) % cyclesPerTick;
		// TODO: Send message to worker thread
		cyclesDone.set(cyclesDone.get() + cycles);
		Platform.runLater(() -> mode.workDone(this));	// simulate a response from worker thread
	}
	public HBox getButtons() {
		return buttons;
	}
	public ReadOnlyLongProperty elapsedProperty() { return elapsed.getReadOnlyProperty(); }
	public long getElapsed() { return elapsed.get(); }
	public ReadOnlyLongProperty cyclesProperty() { return cyclesDone.getReadOnlyProperty(); }
	public long getCycles() { return cyclesDone.get(); }
	public ReadOnlyLongProperty ticksProperty() { return ticksDone.getReadOnlyProperty(); }
	public long getTicks() { return ticksDone.get(); }
}
