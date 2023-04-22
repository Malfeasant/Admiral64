package us.malfeasant.admiral64;

import org.tinylog.Logger;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import us.malfeasant.admiral64.configuration.Configuration;
import us.malfeasant.admiral64.configuration.Dialogs;

public class App extends Application {
    private final ListView<Configuration> listView = new ListView<>(FXCollections.observableArrayList());

    @Override
    public void start(Stage stage) throws Exception {
        Logger.debug("Building window...");

//        var controlbar = new VBox(buildMenu()); // TODO add a toolbar too- new, edit, delete...
        var controlbar = buildMenu();
        var pane = new BorderPane();
        pane.setTop(controlbar);
//        listView = new ListView<>(FXCollections.observableArrayList());
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        pane.setLeft(listView);
        stage.setScene(new Scene(pane));
        stage.show();
    }

    private MenuBar buildMenu() {
        var notSelectedProperty = listView.getSelectionModel().selectedItemProperty().isNull();

        var create = new MenuItem("New...");
        create.setOnAction(e -> handleNew());

        var edit = new MenuItem("Edit...");
        edit.setOnAction(e -> handleEdit());
        edit.disableProperty().bind(notSelectedProperty);
        
        var remove = new MenuItem("Delete...");
        remove.setOnAction(e -> handleDelete());
        remove.disableProperty().bind(notSelectedProperty);

        var start = new MenuItem("Start");
        start.setOnAction(e -> handleStart());
        start.disableProperty().bind(notSelectedProperty);
        
        var open = new MenuItem("Import...");
        open.setOnAction(e -> handleOpen());
        
        var save = new MenuItem("Export...");
        save.setOnAction(e -> handleSave());
        save.disableProperty().bind(notSelectedProperty);
        
        var quit = new MenuItem("Exit");
        quit.setOnAction(e -> handleQuit());

        var sep = new SeparatorMenuItem();
        var file = new Menu("File", null, open, save, sep, quit);
        var machine = new Menu("Machine", null, create, edit, remove, sep, start);
        return new MenuBar(file, machine);
    }

    private void handleNew() {
        Logger.debug("Handling Create");
        var opt = Dialogs.showCreateDialog();
        opt.ifPresent(config -> listView.getItems().add(config));
    }
    private void handleEdit() {
        Logger.debug("Handling Edit");
        var selIndex = listView.getSelectionModel().getSelectedIndex();  // needed to replace item
        // shouldn't need this since we disable the item when nothing is selected- but why tempt fate?
        if (selIndex < 0) return;   // -1 signifies nothing is selected
        var selItem = listView.getSelectionModel().getSelectedItem();
        var opt = Dialogs.showEditDialog(selItem);
        opt.ifPresent(config -> listView.getItems().set(selIndex, config));
    }
    private void handleDelete() {
        Logger.debug("Handling Delete");
        if (listView.getSelectionModel().isEmpty()) return; // can't delete nothing...
        Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure?");
        alert.showAndWait().filter(response -> response == ButtonType.OK)
            .ifPresent(response -> listView.getItems().remove(
                listView.getSelectionModel().getSelectedIndex()));
    }
    private void handleOpen() {
        Logger.debug("Handling Open");
        // TODO
    }
    private void handleSave() {
        Logger.debug("Handling Save");
        if (listView.getSelectionModel().isEmpty()) return; // can't save nothing...
        // TODO
    }
    private void handleQuit() {
        Logger.debug("Handling Quit");
        // TODO
    }
    private void handleStart() {
        Logger.debug("Handling Start");
        if (listView.getSelectionModel().isEmpty()) return; // can't run nothing...
        // TODO
    }

	public static void main(String[] args) {
		Logger.debug("Starting JavaFX App...");
		launch(args);
	}
}
