package us.malfeasant.admiral64.machine;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Contains all the user interface bits of a machine
 */
public class Console {
    private final Machine machine;
    private final Stage stage;

    public Console(Machine machine) {
        this.machine = machine;
        var display = new Display();
        var window = new BorderPane(display.getPane());

        // TODO some menus- at least speed control 

        stage = new Stage(StageStyle.DECORATED);
        stage.setTitle(machine.config.name);
        stage.setScene(new Scene(window));
    }

    public void show() {
        stage.show();
    }
}
