package us.malfeasant.admiral64.timing;

import javafx.scene.control.Button;

enum RunMode {
	STEP("|>") {
		@Override
		protected void modeClicked(TimingGenerator tg) {
			if (tg.mode == this) {	// Already in step mode, run a step
				tg.runFor(1);
			} else {	// Changing from another mode, 
				changeMode(tg, this);
			}
		}
	},
	REAL(">") {
		@Override
		protected void timerFired(TimingGenerator tg) {
			long c = (tg.interval * tg.osc.cycles + tg.remainder) / (tg.osc.seconds * 1000000000L);
			tg.remainder = (tg.interval * tg.osc.cycles + tg.remainder) % (tg.osc.seconds * 1000000000L);
			tg.runFor((int)c);
		}
	},
	FAST(">>") {
		@Override
		protected void setup(TimingGenerator tg) {
			tg.runFor(tg.osc.cycles);	// run first batch, further batches will be run after work comes back
		}
		@Override
		protected void workDone(TimingGenerator tg) {
			tg.runFor(tg.osc.cycles);
		}
	};
	RunMode(String butText) {
		this.butText = butText;
	}
	private final String butText;
	
	Button makeButton(TimingGenerator tg) {
		Button b = new Button(butText);	// TODO: graphic?
		b.setOnAction((event) -> modeClicked(tg));
		return b;
	}
	protected void modeClicked(TimingGenerator tg) {
		if (tg.mode != this) {
			changeMode(tg, this);
		}
	}
	private static void changeMode(TimingGenerator tg, RunMode to) {
		tg.mode.teardown(tg);	// tear down old mode
		tg.mode = to;
		to.setup(tg);
	}
	protected void setup(TimingGenerator tg) {}	// called when mode is set, do any necessary preparation
	protected void workDone(TimingGenerator tg) {}	// called when worker thread has completed work
	protected void timerFired(TimingGenerator tg) {}	// called when animation timer fires
	protected void teardown(TimingGenerator tg) {	// called before new mode is set, do any cleanup
		// reset cycles per second fields
		tg.elapsed = 0;
		tg.cyclesDone = 0;
	}
}
