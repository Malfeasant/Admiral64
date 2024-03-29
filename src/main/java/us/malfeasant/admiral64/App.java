package us.malfeasant.admiral64;

import org.tinylog.Logger;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import us.malfeasant.admiral64.configuration.Dialogs;
import us.malfeasant.admiral64.machine.Machine;

public class App extends Application {
    private final ListView<Machine> listView = new ListView<>(FXCollections.observableArrayList());
    
    @Override
    public void start(Stage stage) throws Exception {
        Logger.debug("Building window...");

//        var controlbar = new VBox(buildMenu()); // TODO add a toolbar too- new, edit, delete...
        var controlbar = buildMenu();
        var pane = new BorderPane();
        pane.setTop(controlbar);
        
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setCellFactory(lv -> {
            return new ListCell<>() {
                @Override
                protected void updateItem(Machine item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item == null ? "" : item.config.cellLabel());
                }
            };
        });
        pane.setLeft(listView);
        stage.setScene(new Scene(pane));
        stage.show();
    }

    private MenuBar buildMenu() {
        var selected = listView.getSelectionModel().selectedItemProperty();
        var notSelectedBinding = selected.isNull();
        var runningBinding = Bindings.selectBoolean(selected.flatMap(Machine::runningProperty));

        var create = new MenuItem("New...");
        create.setOnAction(e -> handleNew());

        var edit = new MenuItem("Edit...");
        edit.setOnAction(e -> handleEdit());
        edit.disableProperty().bind(notSelectedBinding.or(runningBinding));
        
        var remove = new MenuItem("Delete...");
        remove.setOnAction(e -> handleDelete());
        remove.disableProperty().bind(notSelectedBinding.or(runningBinding));

        var start = new MenuItem("Start");
        start.setOnAction(e -> handleStart());
        start.disableProperty().bind(notSelectedBinding.or(runningBinding));

        var freeze = new MenuItem("Freeze");
        freeze.setOnAction(e -> handleFreeze());
        freeze.disableProperty().bind(notSelectedBinding.or(runningBinding.not()));

        var thaw = new MenuItem("Unfreeze");
        thaw.setOnAction(e -> handleThaw());
        thaw.disableProperty().bind(notSelectedBinding.or(runningBinding));

        var stop = new MenuItem("Stop");
        stop.setOnAction(e -> handleStop());
        stop.disableProperty().bind(notSelectedBinding.or(runningBinding.not()));
        
        var open = new MenuItem("Import...");
        open.setOnAction(e -> handleOpen());
        
        var save = new MenuItem("Export...");
        save.setOnAction(e -> handleSave());
        save.disableProperty().bind(notSelectedBinding);
        
        var quit = new MenuItem("Exit");
        quit.setOnAction(e -> handleQuit());

        var file = new Menu("File", null, open, save, new SeparatorMenuItem(), quit);
        var machine = new Menu("Machine", null, create, edit, remove, new SeparatorMenuItem(), start, freeze, stop);
        return new MenuBar(file, machine);
    }

    private void handleNew() {
        Logger.debug("Handling Create");
        var opt = Dialogs.showCreateDialog();
        opt.ifPresent(config -> listView.getItems().add(new Machine(config)));
    }
    private void handleEdit() {
        Logger.debug("Handling Edit");
        var selIndex = listView.getSelectionModel().getSelectedIndex();  // needed to replace item
        // shouldn't need this since we disable the item when nothing is selected- but why tempt fate?
        if (selIndex < 0) return;   // -1 signifies nothing is selected
        var selItem = listView.getSelectionModel().getSelectedItem();
        var opt = Dialogs.showEditDialog(selItem.config);
        opt.ifPresent(config -> listView.getItems().set(selIndex, new Machine(config)));
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
        listView.getSelectionModel().getSelectedItem().start();
    }
    private void handleFreeze() {
        Logger.debug("Handling Suspend");
        if (listView.getSelectionModel().isEmpty()) return;
        listView.getSelectionModel().getSelectedItem().freeze();
    }
    private void handleThaw() {
        Logger.debug("Handling Resume");
        if (listView.getSelectionModel().isEmpty()) return;
        listView.getSelectionModel().getSelectedItem().thaw();
    }
    private void handleStop() {
        Logger.debug("Handling Stop");
        if (listView.getSelectionModel().isEmpty()) return;
        listView.getSelectionModel().getSelectedItem().stop();
    }

	public static void main(String[] args) {
		Logger.debug("Starting JavaFX App...");
		launch(args);
	}
}
