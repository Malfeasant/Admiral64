package us.malfeasant.admiral64.timing;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

/**
 *	generates the timing events that keep the machine running.  Minimum options should be single CPU cycle,  
 *	realtime, or uninhibited.  
 */
public class TimingGenerator {
	private final HBox buttons = new HBox();
	private final Map<RunMode.Mode, RunMode> modes;
	final Oscillator osc = Oscillator.NTSC;	// TODO: make this configurable
	
	public TimingGenerator() {
		Map<RunMode.Mode, RunMode> map = new EnumMap<>(RunMode.Mode.class);
		for (RunMode.Mode m : RunMode.Mode.values()) {
			map.put(m, m.makeMode(this));
			buttons.getChildren().add(m.makeButton(this));
		}
		buttons.setAlignment(Pos.CENTER);
		modes = Collections.unmodifiableMap(map);
		mode = modes.get(RunMode.Mode.STEP);
	}
	// If we want the machine to start in the running state, make it part of the machine startup code, not here.
	RunMode mode;
	
	void step() {
		// fire a step
		System.out.println("Step clicked");
	}
	void setMode(RunMode.Mode m) {
		System.out.print("Mode changing from " + mode);
		mode.teardown();
		mode = modes.get(m);
		mode.setup();
		System.out.println(" to " + mode);
	}
	public HBox getButtons() {
		return buttons;
	}
}
