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
	// Most of these must be longs because System.currentTimeMillis() returns long, and rather than casting...
	private long last;	// last interval timestamp
	private long interval;	// last interval's actual duration
	private long target;	// when next interval should happen
	private long cyclesSinceTick;	// cycles scaled by a factor, used for ongoing cycles per tick calculation 
	private int cycleIntervalRem;	// used in realtime calculation to add cycles on occasion (likely only matters for PAL)
	private final Machine machine;
	
	private final HBox buttons = new HBox();
	private final Oscillator osc;
	private final Powerline pow;
	private final int cyclesPerInterval;
	private final int cyclesPerIntervalRem;
	
	
	// might end up moving these to a new class- monitor? debug?
	private final ReadOnlyLongWrapper cyclesDone = new ReadOnlyLongWrapper();	// total cycles run since arbitrary point
	private final ReadOnlyLongWrapper ticksDone = new ReadOnlyLongWrapper();	//	Total number of power cycles fired
	private final ReadOnlyLongWrapper elapsed = new ReadOnlyLongWrapper();	// total time since arbitrary point
	
	public TimingGenerator(Oscillator o, Powerline p, Machine m, WorkQueue.WorkSender s) {
		machine = m;
		osc = o;
		pow = p;
		
		cyclesPerInterval = osc.cycles / 1000;
		cyclesPerIntervalRem = osc.cycles % 1000;
		
		for (RunMode mode : RunMode.values()) {
			buttons.getChildren().add(mode.makeButton(e -> {
				s.changeMode(mode);
			}));
		}
		buttons.setAlignment(Pos.CENTER);
	}
	
	// this will be called on the worker thread
	public void run(RunMode mode) {
		long now = System.currentTimeMillis();	// ms accuracy should be good enough
		interval = last == 0 ? osc.seconds : now - last;	// If first run, pretend interval was dead on, otherwise calculate
		last = now;
		
		int cycles = mode == RunMode.STEP ? 1 : cyclesPerInterval;
		if (mode == RunMode.REAL) {
			cycleIntervalRem += cyclesPerIntervalRem;
			cycles += cycleIntervalRem / 1000;	// remainder has accumulated to need a new cycle- should only ever add 1
			cycleIntervalRem %= 1000;
		}
		
		machine.cycle(cycles);
		
		int ticks = 0;
		cyclesSinceTick += cycles * osc.seconds * pow.cycles;
		while (cyclesSinceTick > osc.cycles) {	// should be unusual for this to loop more than once
			ticks++;
			cyclesSinceTick -= osc.cycles;
			machine.tick();
		}
		
		if (mode == RunMode.REAL) {
			if (target == 0) {	// first run of realtime so do some setup
				target = System.currentTimeMillis();
			}
			target += osc.seconds;
			// Figure out how long to sleep
			long diff = target - System.currentTimeMillis();	// compute time left in current interval
			if (diff > 0) {	// if we're falling short of target, don't sleep...
				try {
					Thread.sleep(diff);
				} catch (InterruptedException e) {}
			}
		} else {
			target = 0;	// reset realtime calculation
			try {
				Thread.sleep(100);	// Don't really want to sleep, but if we don't, GUI thread starves...  TODO fix it
			} catch (InterruptedException e) {}
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
