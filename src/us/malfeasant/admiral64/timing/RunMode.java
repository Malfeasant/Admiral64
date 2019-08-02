package us.malfeasant.admiral64.timing;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public enum RunMode {
	STEP("|>"),
	REAL(">"),
	FAST(">>");
	RunMode(String butText) {
		this.butText = butText;
	}
	private final String butText;
	
	Button makeButton(EventHandler<ActionEvent> e) {
		Button b = new Button(butText);	// TODO: graphic?
		b.setOnAction(e);
		return b;
	}
	
	public static RunMode getDefault() {
		return STEP;
	}
}
