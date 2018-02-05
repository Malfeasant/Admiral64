package us.malfeasant.admiral64.timing;

import javafx.animation.AnimationTimer;

class Real extends RunMode {
	private long frames;
	private long millis;
	private long last;
	private int targetCycles;
	
	AnimationTimer timer = new AnimationTimer() {
		@Override
		public void handle(long now) {
			if (last < 0) {	// first frame since started, so no elapsed time
				last = now;
				return;	// skip this iteration
			}
			long elapsed = now - last;
			frames++;
			millis += elapsed;
		}
	};
	
	Real(TimingGenerator tg) {
		super(tg);
	}
	@Override
	void setup() {
		frames = 0;
		millis = 0;
		last = -1;
		targetCycles = timingGenerator.osc.cycles;
	}

	@Override
	void teardown() {
		timer.stop();
	}
}
