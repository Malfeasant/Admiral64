package us.malfeasant.admiral64;

import org.tinylog.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import us.malfeasant.admiral64.configuration.Configuration;
import us.malfeasant.admiral64.configuration.ConfigurationBuilder;
import us.malfeasant.admiral64.configuration.Oscillator;
import us.malfeasant.admiral64.configuration.Power;

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
        var dialog = new Dialog<Configuration>();
        dialog.setTitle("Build new machine");
        var createType = new ButtonType("Create", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createType, ButtonType.CANCEL);
        var pane = new GridPane();
        var nameField = new TextField();
        nameField.setPromptText("Enter machine name:");
        pane.add(nameField, 0, 0);

        var oscBox = new ChoiceBox<Oscillator>();
        oscBox.getItems().addAll(Oscillator.values());
        oscBox.getSelectionModel().clearAndSelect(0);
        var powBox = new ChoiceBox<Power>();
        powBox.getItems().addAll(Power.values());
        powBox.getSelectionModel().clearAndSelect(0);
        pane.add(oscBox, 1, 0);
        pane.add(powBox, 2, 0);

        var builder = new ConfigurationBuilder();
        builder.nameProperty.bind(nameField.textProperty());
        // TODO builder.oscillatorProperty.bind(oscBox.. how?);
        dialog.getDialogPane().setContent(pane);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == createType) {
                Logger.debug("Creating new machine named " + builder.nameProperty.get());
                return builder.makeFrom();
            }
            Logger.debug("Looks like user cancelled machine creation...");
            return null;
        });
        dialog.showAndWait();   // TODO make use of it...
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
