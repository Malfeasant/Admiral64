package us.malfeasant.admiral64.timing;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import javafx.scene.Group;

/**
 *	generates the timing events that keep the machine running.  Minimum options should be single CPU cycle,  
 *	realtime, or uninhibited.  
 */
public class TimingGenerator {
	private final Group buttons = new Group();
	private final Map<RunMode.Mode, RunMode> modes;
	public TimingGenerator() {
		Map<RunMode.Mode, RunMode> map = new EnumMap<>(RunMode.Mode.class);
		for (RunMode.Mode m : RunMode.Mode.values()) {
			map.put(m, m.makeMode(this));
			buttons.getChildren().add(m.makeButton(this));
		}
		modes = Collections.unmodifiableMap(map);
	}
	// If we want the machine to start in the running state, make it part of the machine startup code, not here.
	RunMode mode;
	
	void step() {
		// fire a step
	}
	void setMode(RunMode.Mode m) {
		mode.teardown();
		mode = modes.get(m);
		mode.setup();
	}
}
