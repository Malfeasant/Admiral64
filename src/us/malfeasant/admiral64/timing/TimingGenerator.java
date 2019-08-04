package us.malfeasant.admiral64.timing;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import us.malfeasant.admiral64.machine.Machine;
import us.malfeasant.admiral64.worker.WorkQueue;

/**
 *	generates the timing events that keep the machine running.  Minimum options should be single CPU cycle,  
 *	realtime, or uninhibited. 
 */
public class TimingGenerator {
	// Most of these must be longs because timestamps in nanoseconds would overflow an int in 2 seconds.
	// Even cpu cycles would overflow in 34 minutes.  RTC ticks could go over a year, but
	// running at high speed could conceivably reach that in sim time...
	private long last;	// last frame timestamp
	private final ReadOnlyLongWrapper elapsed = new ReadOnlyLongWrapper();	// total time since arbitrary point
	private long interval;	// current frame's duration
	private final ReadOnlyLongWrapper cyclesDone = new ReadOnlyLongWrapper();	// total cycles run since arbitrary point
	private long cycleRem;	// leftover time to run in next interval (only used for realtime)
	private final int cyclesPerTick;	// integer part of how many cpu cycles per RTC tick
	private final int cyclesPerTickRem;	// remainder of above
	private final ReadOnlyLongWrapper ticksDone = new ReadOnlyLongWrapper();	//	Total number of power cycles fired
	private long cyclesSinceTick; // not actually cycles, used for ongoing cycles per tick calculation 
	private RunMode lastMode;	// is this really needed?  only to reset realtime calculation, could be better way...
	private final Machine machine;
	
	private final HBox buttons = new HBox();
	private final Oscillator osc;
	private final Powerline pow;
	
	public TimingGenerator(Oscillator o, Powerline p, Machine m, WorkQueue.WorkSender s) {
		machine = m;
		osc = o;
		pow = p;
		cyclesPerTick = osc.cycles / (osc.seconds * pow.cycles);
		cyclesPerTickRem = osc.cycles % (osc.seconds * pow.cycles);
		for (RunMode mode : RunMode.values()) {
			buttons.getChildren().add(mode.makeButton(e -> {
				s.changeMode(mode);
			}));
		}
		buttons.setAlignment(Pos.CENTER);
	}
	
	// this will be called on the worker thread
	public void run(RunMode mode) {
		long now = System.nanoTime();
		interval = last == 0 ? 1 : now - last;	// If first run, pretend interval is 1 ns, otherwise calculate
		last = now;
		
		int cycles = mode == RunMode.STEP ? 1 : 0x2000;
		machine.cycle(cycles);
		
		int ticks = 0;
		cyclesSinceTick += cycles * osc.seconds * pow.cycles;
		while (cyclesSinceTick > osc.cycles) {
			ticks++;
			cyclesSinceTick -= osc.cycles;
			machine.tick();
		}
		
		if (mode == RunMode.REAL) {
			// Figure out how long to sleep
			cycleRem += cycles * osc.seconds;
			long targetTime = cycleRem * 1000000000 / osc.cycles;
			cycleRem = cycleRem % osc.cycles;
		}
		
		// update debug counters
		propHelper(cyclesDone, cycles);
		propHelper(ticksDone, ticks);
		propHelper(elapsed, interval);
	}
	// Can't do this within the above method, because the number can't be final...
	private void propHelper(ReadOnlyLongWrapper wrapper, long num) {
		Platform.runLater(() -> {	// Pass to GUI thread
			wrapper.set(wrapper.get() + num);
		});
	}
	public void reset() {
		cyclesDone.set(0);
		ticksDone.set(0);
		elapsed.set(0);
	}
	
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
