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
	private final ReadOnlyLongWrapper ticksDone = new ReadOnlyLongWrapper();	//	Total number of power cycles (used to tick CIAs' RTC)
	private int tickRem;	// remainder from above calculation
	private RunMode mode;
	
	private final HBox buttons = new HBox();
	private final Oscillator osc;
	private final Powerline pow;
	
	public TimingGenerator(Oscillator o, Powerline p, Machine m, WorkQueue.WorkSender s) {
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
		long cycles = 0;
		// TODO figure out how many cycles to run, then how long/if sleep before returning
		switch (mode) {
		case STEP:
			cycles = 1;
			System.out.println("Taking a step");
			break;
		case REAL:
			cycles = 1000;	// TODO not really
			System.out.println("Realtime");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Don't need to do anything
			}
			break;
		case FAST:
			cycles = 0x2000;	// TODO not really
			try {
				Thread.sleep(1);	// pretend to do some work
			} catch (InterruptedException e) {
				// Don't need to do anything
			}
			break;
		}
		propHelper(cyclesDone, cycles);
		propHelper(elapsed, interval);
	}
	// Can't do this within the above method, because the number can't be final...
	private void propHelper(ReadOnlyLongWrapper wrapper, long num) {
		Platform.runLater(() -> {	// Pass to GUI thread
			wrapper.set(wrapper.get() + num);
		});
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
