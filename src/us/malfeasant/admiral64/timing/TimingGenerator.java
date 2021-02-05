package us.malfeasant.admiral64.timing;

import java.util.concurrent.CopyOnWriteArraySet;

import javafx.application.Platform;
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
	// Most of these must be longs because System.currentTimeMillis() returns long, and rather than casting...
	private long last;	// last interval timestamp
	private long cyclesSinceTick;	// cycles scaled by a factor, used for ongoing cycles per tick calculation 
	private int cycleIntervalRem;	// used in realtime calculation to add cycles on occasion (only matters for PAL)
	private final CopyOnWriteArraySet<PowerConsumer> powerConsumers;
	private final CopyOnWriteArraySet<CrystalConsumer> crystalConsumers;
	
	private final HBox buttons = new HBox();
	private final Oscillator osc;
	private final Powerline pow;
	private final int cyclesPerInterval;
	private final int cyclesPerIntervalRem;
	
	// might end up moving these to a new class- monitor? debug?
	private final ReadOnlyLongWrapper cyclesDone = new ReadOnlyLongWrapper();	// total cycles run since arbitrary point
	private final ReadOnlyLongWrapper ticksDone = new ReadOnlyLongWrapper();	//	Total number of power cycles fired
	private final ReadOnlyLongWrapper elapsed = new ReadOnlyLongWrapper();	// total time since arbitrary point
	
	public TimingGenerator(Oscillator o, Powerline p, WorkQueue.WorkSender s) {
		powerConsumers = new CopyOnWriteArraySet<PowerConsumer>();
		crystalConsumers = new CopyOnWriteArraySet<CrystalConsumer>();
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
		long interval = last == 0 ? osc.seconds : now - last;	// If first run, pretend interval was dead on, otherwise calculate
		last = now;
		
		int cycles = mode == RunMode.STEP ? 1 : cyclesPerInterval;
		if (mode == RunMode.REAL) {
			cycleIntervalRem += cyclesPerIntervalRem;
			cycles += cycleIntervalRem / 1000;	// remainder has accumulated to need a new cycle- should only ever add 1
			cycleIntervalRem %= 1000;
		}
		
		for (int cycle = 0; cycle < cycles; cycle++) {
			for (CrystalConsumer cc : crystalConsumers) {
				cc.negEdge();
				cc.posEdge();
			}
		}
		
		int ticks = 0;
		cyclesSinceTick += cycles * osc.seconds * pow.cycles;
		while (cyclesSinceTick > osc.cycles) {	// should be unusual for this to loop more than once
			ticks++;
			cyclesSinceTick -= osc.cycles;
			for (PowerConsumer pc : powerConsumers) {
				pc.tick();
			}
		}
		
		if (mode == RunMode.REAL) {
			// Figure out how long to sleep
			long diff = osc.seconds - (System.currentTimeMillis() % osc.seconds);
			try {
				Thread.sleep(diff);
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
	public void resetDebugCounters() {
		cyclesDone.set(0);
		ticksDone.set(0);
		elapsed.set(0);
	}
	
	// Threadsafe add listener for cpu clock events
	public void addCrystalConsumer(CrystalConsumer cc) {
		crystalConsumers.add(cc);
	}
	public void removeCrystalConsumer(CrystalConsumer cc) {
		crystalConsumers.remove(cc);
	}
	
	// Threadsafe add listener for powerline events
	public void addPowerConsumer(PowerConsumer pc) {
		powerConsumers.add(pc);
	}
	public void removePowerConsumer(PowerConsumer pc) {
		powerConsumers.remove(pc);
	}
	
	public Node getButtons() {
		return buttons;
	}
	// TODO: Migrate these debug counters out of this class- should be able to add
	// power and crystal listeners just like the various chips...
	public ReadOnlyLongProperty elapsedProperty() { return elapsed.getReadOnlyProperty(); }
	public long getElapsed() { return elapsed.get(); }
	public ReadOnlyLongProperty cyclesProperty() { return cyclesDone.getReadOnlyProperty(); }
	public long getCycles() { return cyclesDone.get(); }
	public ReadOnlyLongProperty ticksProperty() { return ticksDone.getReadOnlyProperty(); }
	public long getTicks() { return ticksDone.get(); }
}
