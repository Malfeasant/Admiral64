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
	private long interval;	// current frame's duration
	private long cycleRem;	// leftover time to run in next interval (only used for realtime)
	private long cyclesSinceTick; // not actually cycles, used for ongoing cycles per tick calculation 
	private final Machine machine;
	
	private final HBox buttons = new HBox();
	private final Oscillator osc;
	private final Powerline pow;
	
	// might end up moving these to a new class- monitor? debug?
	private final ReadOnlyLongWrapper cyclesDone = new ReadOnlyLongWrapper();	// total cycles run since arbitrary point
	private final ReadOnlyLongWrapper ticksDone = new ReadOnlyLongWrapper();	//	Total number of power cycles fired
	private final ReadOnlyLongWrapper elapsed = new ReadOnlyLongWrapper();	// total time since arbitrary point
	
	public TimingGenerator(Oscillator o, Powerline p, Machine m, WorkQueue.WorkSender s) {
		machine = m;
		osc = o;
		pow = p;
//		cyclesPerTick = osc.cycles / (osc.seconds * pow.cycles);
//		cyclesPerTickRem = osc.cycles % (osc.seconds * pow.cycles);
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
		interval = last == 0 ? 0x4000000 : now - last;	// If first run, pretend interval is ~16ms, otherwise calculate
		last = now;
		
		int cycles = mode == RunMode.STEP ? 1 : 0x4000;
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
			long targetTime = cycles * osc.seconds * 1000 / osc.cycles;
			cycleRem = cycles * osc.seconds * 1000 % osc.cycles;
			long diff = targetTime - ((System.nanoTime() - last) / 1000000);
			if (diff > 0) {	// if we're falling short of target, don't sleep...
				try {
//					System.out.println("Sleeping for " + diff + " ms");
					Thread.sleep(diff);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			try {
				Thread.sleep(1);	// Don't really want to sleep, but if we don't, GUI thread starves...
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
