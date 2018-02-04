package us.malfeasant.admiral64.timing;

import javafx.scene.control.Button;

abstract class RunMode {
	protected final TimingGenerator timingGenerator;
	abstract void setup();
	abstract void teardown();
	RunMode(TimingGenerator tg) {
		timingGenerator = tg;
	}
	enum Mode {
		STEP("|>") {
			@Override
			RunMode makeMode(TimingGenerator tg) {
				return new Step(tg);
			}
		},
		REAL(">") {
			@Override
			RunMode makeMode(TimingGenerator tg) {
				return new Real(tg);
			}
		},
		FAST(">>") {
			@Override
			RunMode makeMode(TimingGenerator tg) {
				return new Fast(tg);
			}
		};
		Mode(String butText) {
			this.butText = butText;
		}
		private final String butText;
		
		abstract RunMode makeMode(TimingGenerator tg);
		Button makeButton(TimingGenerator tg) {
			Button b = new Button(butText);	// TODO: graphic?
			b.setOnAction((event) -> tg.setMode(this));
			return b;
		}
	}
}
