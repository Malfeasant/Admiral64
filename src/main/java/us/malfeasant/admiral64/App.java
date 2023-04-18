package us.malfeasant.admiral64;

import org.tinylog.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
        var dialog = new Dialog<Configuration>();
        dialog.setTitle("Build new machine");
        var pane = new GridPane();
        var nameField = new TextField();
        nameField.setPromptText("Enter machine name:");
        pane.add(nameField, 0, 0);

        dialog.getDialogPane().setContent(pane);

        dialog.setResultConverter(button -> {
            
        });
    }
    private void handleOpen(ActionEvent e) {

    }
    private void handleEdit(ActionEvent e) {

    }
	public static void main(String[] args) {
		Logger.debug("Starting JavaFX App...");
		launch(args);
	}
}
