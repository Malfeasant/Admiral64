package us.malfeasant.admiral64.timing;

import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import us.malfeasant.admiral64.worker.WorkQueue;

/**
 *	generates the timing events that keep the machine running.  Minimum options should be single CPU cycle,  
 *	realtime, or uninhibited. 
 */
public class TimingGenerator {
	RunMode mode = RunMode.getDefault();
	// Most of these must be longs because timestamps in nanoseconds would overflow an int in 2 seconds.
	// Even cpu cycles would overflow in 34 minutes.  RTC ticks could go over a year, but
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
	
	private final WorkQueue.WorkSender workSender;
	private int workOutstanding = 0;
	
/*	@Override
	public void handle(long now) {
		if (last > 0) {	// not first run
			interval = now - last;
			elapsed.set(elapsed.get() + interval);
			mode.timerFired(this);
			
			// TODO: Render worker thread results to screen
		}	// skip first run
		last = now;
	}
*/	
	public TimingGenerator(Oscillator o, Powerline p, WorkQueue.WorkSender s) {
		workSender = s;
		osc = o;
		pow = p;
		cyclesPerTick = osc.cycles / (osc.seconds * pow.cycles);	// Integer is accurate enough
		for (RunMode m : RunMode.values()) {
			buttons.getChildren().add(m.makeButton(this));
		}
		buttons.setAlignment(Pos.CENTER);
	}
	
	/**
	 *	Intended to be called by the worker thread
	 */
	public void workDone() {
		workOutstanding--;
		if (workOutstanding < 1) mode.workDone(this);
	}
	/*
	void runFor(int cycles) {
		if (tickRem + cycles < cyclesPerTick) {
			workOutstanding++;
			workSender.requestCycles(cycles);
			cyclesDone.set(cyclesDone.get() + cycles);
			tickRem += cycles;
		} else {
			workOutstanding++;
			workSender.requestCycles(cyclesPerTick - tickRem);
			cyclesDone.set(cyclesDone.get() + cyclesPerTick - tickRem);
			cycles -= (cyclesPerTick - tickRem);
			workOutstanding++;
			workSender.requestTick();
			ticksDone.set(ticksDone.get() + 1);
			
			while (cycles >= cyclesPerTick) {
				workOutstanding++;
				workSender.requestCycles(cyclesPerTick);
				cyclesDone.set(cyclesDone.get() + cyclesPerTick);
				cycles -= cyclesPerTick;
				workOutstanding++;
				workSender.requestTick();
				ticksDone.set(ticksDone.get() + 1);
			}
			
			if (cycles > 0) {
				workOutstanding++;
				workSender.requestCycles(cycles);
				cyclesDone.set(cyclesDone.get() + cycles);
			}
			tickRem = cycles;
		}
	}*/
	public Node getButtons() {
		return buttons;
	}
	public ReadOnlyLongProperty elapsedProperty() { return elapsed.getReadOnlyProperty(); }
	public long getElapsed() { return elapsed.get(); }
	public ReadOnlyLongProperty cyclesProperty() { return cyclesDone.getReadOnlyProperty(); }
	public long getCycles() { return cyclesDone.get(); }
	public ReadOnlyLongProperty ticksProperty() { return ticksDone.getReadOnlyProperty(); }
	public long getTicks() { return ticksDone.get(); }
}
