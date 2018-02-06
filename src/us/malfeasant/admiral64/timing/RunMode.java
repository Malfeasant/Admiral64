package us.malfeasant.admiral64.timing;

import javafx.scene.control.Button;

enum RunMode {
	STEP("|>") {
		@Override
		void setup(TimingGenerator tg) {
			tg.runFor(1);
		}
	},
	REAL(">") {
		@Override
		void timerFired(TimingGenerator tg) {
			long c = (tg.interval * tg.osc.cycles + tg.remainder) / (tg.osc.seconds * 1000000000L);
			tg.remainder = (tg.interval * tg.osc.cycles + tg.remainder) % (tg.osc.seconds * 1000000000L);
			tg.runFor((int)c);
		}
	},
	FAST(">>") {
		@Override
		void setup(TimingGenerator tg) {
			tg.runFor(tg.osc.cycles);	// run first batch, further batches will be run after work comes back
		}
		@Override
		void workDone(TimingGenerator tg) {
			tg.runFor(tg.osc.cycles);
		}
	};
	RunMode(String butText) {
		this.butText = butText;
	}
	private final String butText;
	
	Button makeButton(TimingGenerator tg) {
		Button b = new Button(butText);	// TODO: graphic?
		b.setOnAction((event) -> tg.setMode(this));
		return b;
	}
	void setup(TimingGenerator tg) {}	// called when mode is set, do any necessary preparation
	void workDone(TimingGenerator tg) {}	// called when worker thread has completed work
	void timerFired(TimingGenerator tg) {}	// called when animation timer fires
	void teardown(TimingGenerator tg) {	// called before new mode is set, do any cleanup
		// reset cycles per second fields
		tg.elapsed = 0;
		tg.cyclesDone = 0;
	}
}
