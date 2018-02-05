package us.malfeasant.admiral64.timing;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

/**
 *	generates the timing events that keep the machine running.  Minimum options should be single CPU cycle,  
 *	realtime, or uninhibited.  
 */
public class TimingGenerator extends AnimationTimer {
	RunMode mode = RunMode.STEP;
	long last;	// last frame time
	long elapsed;	// total time since arbitrary point
	long interval;	// current frame's duration
	long cyclesDone;	// total cycles run since arbitrary point
	long targetCycles;	// cycles to run in next interval
	private final HBox buttons = new HBox();
	final Oscillator osc = Oscillator.NTSC;	// TODO: make this configurable
	
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
	
	public TimingGenerator() {
		for (RunMode m : RunMode.values()) {
			buttons.getChildren().add(m.makeButton(this));
		}
		buttons.setAlignment(Pos.CENTER);
	}
	
	void runFor(int cycles) {
		// TODO: Send message to worker thread
		cyclesDone += cycles;
		Platform.runLater(() -> mode.workDone(this));	// simulate a response from worker thread
	}
	void setMode(RunMode m) {
		if (m != RunMode.STEP && mode == m) return;	// If old and new are the same, we only care if it's a step
		mode.teardown(this);
		mode = m;
		mode.setup(this);
	}
	public HBox getButtons() {
		return buttons;
	}
}