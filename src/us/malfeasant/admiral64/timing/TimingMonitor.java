package us.malfeasant.admiral64.timing;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;

public class TimingMonitor implements CrystalConsumer, PowerConsumer {
	private final ReadOnlyLongWrapper cyclesDone = new ReadOnlyLongWrapper();	// total cycles run since arbitrary point
	private final ReadOnlyLongWrapper ticksDone = new ReadOnlyLongWrapper();	//	Total number of power cycles fired
	private final ReadOnlyLongWrapper elapsed = new ReadOnlyLongWrapper();	// total time since arbitrary point
	
	public ReadOnlyLongProperty elapsedProperty() { return elapsed.getReadOnlyProperty(); }
	public long getElapsed() { return elapsed.get(); }
	public ReadOnlyLongProperty cyclesProperty() { return cyclesDone.getReadOnlyProperty(); }
	public long getCycles() { return cyclesDone.get(); }
	public ReadOnlyLongProperty ticksProperty() { return ticksDone.getReadOnlyProperty(); }
	public long getTicks() { return ticksDone.get(); }
	
	private long last = System.nanoTime();	// last interval timestamp
	private final Alert alert;
	private final TimingGenerator timing;
	
	private volatile int cycles;	// inefficient to track all cycles in property immediately- accumulate a bunch here, then move them periodically
	private volatile int ticks;	// same as above, but for ticks
	// volatile is "good enough" thread safety- race conditions will at worst make the counts temporarily inaccurate (how bad?)
	private final AnimationTimer updater;	// timer which will grab a batch of cycles and ticks and update their properties
	
	public TimingMonitor(TimingGenerator tgen) {
		updater = new AnimationTimer() {
			@Override
			public void handle(long now) {
				long interval = now - last;
				last = now;
				
				int cyclesNow = cycles;
				cycles = 0;
				
				int ticksNow = ticks;
				ticks = 0;
				
				cyclesDone.set(cyclesDone.get() + cyclesNow);
				ticksDone.set(ticksDone.get() + ticksNow);
				elapsed.set(elapsed.get() + interval);			
			}
		};
		timing = tgen;
		alert = new Alert(AlertType.INFORMATION);
		alert.initModality(Modality.NONE);
		alert.setTitle("Timing monitor");
		alert.setHeaderText("");
		var seconds = elapsed.divide(1e9);	// convert nanoseconds to seconds
		alert.contentTextProperty().bind(Bindings.format(
				"%d cycles in %.0f seconds: %.9fMHz\n%d ticks in %.0f seconds: %.3fHz",
				cyclesDone,
				seconds,
				cyclesDone.divide(elapsed.divide(1e3)),	// so we get mhz instead of hz
				ticksDone,
				seconds,
				ticksDone.divide(seconds)));
		
		tgen.addCrystalConsumer(this);
		tgen.addPowerConsumer(this);
		alert.setOnHidden(event -> {	// make sure to remove listeners or else gremlins attack
			updater.stop();
			timing.removeCrystalConsumer(this);
			timing.removePowerConsumer(this);
		});
		alert.show();
		updater.start();
//		elapsed.get();	// otherwise it never gets invalidated, because it was never valid to begin with
	}
	@Override
	public void tick() {
		++ticks;
//		System.out.println("Ticks: " + ticksDone.get() + "\tTime: " + elapsed.get());
	}
	@Override
	public void posEdge() {
		++cycles;
//		Platform.runLater(() -> cyclesDone.set(cyclesDone.get() + 1));
	}
	@Override
	public void negEdge() {}	// only count positive edges
}
