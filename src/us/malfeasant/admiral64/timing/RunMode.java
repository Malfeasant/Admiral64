package us.malfeasant.admiral64.timing;

import javafx.scene.control.Button;

enum RunMode {
	STEP("|>") {
		@Override
		void setup(TimingGenerator tg) {
			// deliberately not calling super.setup(tg) as there's no point to reset the calc with every step
			tg.runFor(1);
		}
	},
	REAL(">") {
		@Override
		void setup(TimingGenerator tg) {
			super.setup(tg);
			tg.runFor(tg.targetCycles);	// Have to guess for first batch, otherwise rate adjust divides by 0.
		}
		@Override
		void timerFired(TimingGenerator tg) {
			double targetRate = tg.osc.cyclesPerSecond;
			double actualRate = tg.cyclesDone * 1e9 / tg.elapsed;
			double slip = targetRate / actualRate;
			tg.targetCycles *= slip;
			tg.runFor(tg.targetCycles);
			System.out.println("Actual: " + actualRate + "\tTarget: " + targetRate);
			System.out.println("Slip is " + slip + "\tRunning for " + tg.targetCycles + " cycles.");
		}
	},
	FAST(">>") {
		@Override
		void setup(TimingGenerator tg) {
			super.setup(tg);
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
	void setup(TimingGenerator tg) {	// called when mode is set, do any necessary preparation
		// reset cycles per second fields
		tg.elapsed = 0;
		tg.cyclesDone = 0;
	}
	void workDone(TimingGenerator tg) {}	// called when worker thread has completed work
	void timerFired(TimingGenerator tg) {}	// called when animation timer fires
	void teardown(TimingGenerator tg) {	// called before new mode is set, do any cleanup
		System.out.println("Ran " + tg.cyclesDone + " cycles in " + tg.elapsed / 1e9 + " seconds.");
		System.out.println("  That's " + tg.cyclesDone * 1e9 / tg.elapsed + " cycles per second.");
	}
}
