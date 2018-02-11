package us.malfeasant.admiral64.console;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Status {
	private final LongProperty cycles = new SimpleLongProperty();
	private final LongProperty ticks = new SimpleLongProperty();
	private final LongProperty elapsed = new SimpleLongProperty();
	private final Node node;
	
	public Status() {
		Text cyclesText = new Text();
		Text ticksText = new Text();
		cycles.addListener((observable, oldValue, newValue) -> {
			if (elapsed.get() > 0) // Avoid ugly divide by zero
				cyclesText.setText(String.format("%d cycles in %d seconds: %.9fMHz",
						cycles.get(), elapsed.get() / 1000000000, cycles.get() * 1e3 / elapsed.get() ));
			}
		);
		ticks.addListener((observable, oldValue, newValue) -> {
			if (elapsed.get() > 0) // Avoid ugly divide by zero
				ticksText.setText(String.format("%d ticks in %d seconds: %.3fHz",
						ticks.get(), elapsed.get() / 1000000000, ticks.get() * 1e9 / elapsed.get() ));
		});
		node = new VBox(cyclesText, ticksText);
	}
	
	public LongProperty cyclesProperty() { return cycles; }
	public LongProperty ticksProperty() { return ticks; }
	public LongProperty elapsedProperty() { return elapsed; }
	public Node getNode() {
		return node;
	}

	@Override
	protected void finalize() throws Throwable {
		System.out.println("Status was garbage collected.");
		super.finalize();
	}
}
