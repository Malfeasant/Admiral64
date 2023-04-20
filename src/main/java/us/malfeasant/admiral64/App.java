package us.malfeasant.admiral64;

import org.tinylog.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import us.malfeasant.admiral64.configuration.Dialogs;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Logger.debug("Building window...");

//        var controlbar = new VBox(buildMenu()); // TODO add a toolbar too- new, edit, delete...
        var controlbar = buildMenu();
        var pane = new BorderPane();
        pane.setTop(controlbar);
        stage.setScene(new Scene(pane));
        stage.show();
    }

    private MenuBar buildMenu() {
        var create = new MenuItem("New...");
        create.setOnAction(e -> handleNew(e));
        var open = new MenuItem("Import...");
        open.setOnAction(e -> handleOpen(e));
        var edit = new MenuItem("Edit...");
        edit.setOnAction(e -> handleEdit(e));
        var file = new Menu("File", null, create, open, edit);
        return new MenuBar(file);
    }

    private void handleNew(ActionEvent e) {
        Logger.debug("Handling Create");
        Dialogs.showCreateDialog();
    }
    private void handleOpen(ActionEvent e) {
        Logger.debug("Handling Open");
        // TODO
    }
    private void handleEdit(ActionEvent e) {
        Logger.debug("Handling Edit");
        // TODO
    }
	public static void main(String[] args) {
		Logger.debug("Starting JavaFX App...");
		launch(args);
	}
}
