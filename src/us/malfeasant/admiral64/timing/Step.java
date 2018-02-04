package us.malfeasant.admiral64.timing;

class Step extends RunMode {
	Step(TimingGenerator tg) {
		super(tg);
	}
	@Override
	void setup() {
		timingGenerator.step();
	}
	@Override
	void teardown() {}	// no teardown for Step
}
