package us.malfeasant.admiral64.timing;

import javafx.application.Platform;
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
	
	private long last = System.currentTimeMillis();	// last interval timestamp
	private final Alert alert;
	private final TimingGenerator timing;
	
	private int cycles;	// inefficient to track all cycles in property immediately- accumulate a bunch here, then move them periodically
	
	public TimingMonitor(TimingGenerator tgen) {
		timing = tgen;
		alert = new Alert(AlertType.INFORMATION);
		alert.initModality(Modality.NONE);
		alert.setTitle("Timing monitor");
		alert.setHeaderText("");
		alert.contentTextProperty().bind(Bindings.format(
				"%d cycles in %d seconds: %.9fMHz\n%d ticks in %d seconds: %.3fHz",
				cyclesDone,
				elapsed.divide(1000l),
				cyclesDone.divide(elapsed.multiply(1e3)),
				ticksDone,
				elapsed.divide(1000),
				ticksDone.multiply(1e3).divide(elapsed)));
		
		tgen.addCrystalConsumer(this);
		tgen.addPowerConsumer(this);
		alert.setOnHidden(event -> {	// make sure to remove listeners or else gremlins attack
			timing.removeCrystalConsumer(this);
			timing.removePowerConsumer(this);
		});
		alert.show();
		elapsed.get();	// otherwise it never gets invalidated, because it was never valid to begin with
	}
	@Override
	public void tick() {
		long now = System.currentTimeMillis();	// ms accuracy should be good enough
		long interval = now - last;
		last = now;
		
		int cyclesNow = cycles;
		cycles = 0;
		
		Platform.runLater(() -> {
			cyclesDone.set(cyclesDone.get() + cyclesNow);
			ticksDone.set(ticksDone.get() + 1);
			elapsed.set(elapsed.get() + interval);			
		});
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
