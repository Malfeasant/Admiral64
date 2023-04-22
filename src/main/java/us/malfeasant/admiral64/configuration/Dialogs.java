package us.malfeasant.admiral64.configuration;

import java.util.Optional;
import org.tinylog.Logger;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Helper class- static methods to build, display, and return input from dialogs
 */
public class Dialogs {

    /**
     * Displays a (very primative) dialog to setup a new machine with default options
     * 
     * @return a new Configuration object reflecting user's choices
     */
    public static Optional<Configuration> showCreateDialog() {
        return showDialog(null);
    }

    /**
     * Displays a (very primative) dialog to edit an existing machine
     * 
     * @param currentConfiguration an existing object to preload the dialog
     * @return a new Configuration object reflecting user's choices
     */
    public static Optional<Configuration> showEditDialog(Configuration currentConfiguration) {
        return showDialog(currentConfiguration);
    }

    /**
     * Nifty way to make a lazily initialized singleton object without
     * having to worry about any concurrency gotchas
     */
    private enum ConfigDialog {
        INSTANCE;

        private final Dialog<Configuration> dialog;
        private final TextField nameField;
        private final ChoiceBox<Oscillator> oscBox;
        private final ChoiceBox<Power> powBox;

        ConfigDialog() {
            dialog = new Dialog<>();
            nameField = new TextField();
            nameField.setPromptText("Machine name:");
            oscBox = new ChoiceBox<Oscillator>();
            oscBox.getItems().addAll(Oscillator.values());
            powBox = new ChoiceBox<Power>();
            powBox.getItems().addAll(Power.values());

            var pane = new GridPane();
            pane.add(nameField, 0, 0);
            pane.add(oscBox, 1, 0);
            pane.add(powBox, 2, 0);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);
            dialog.getDialogPane().setContent(pane);
        }
    }

    private static Optional<Configuration> showDialog(Configuration oldConfiguration) {
        var I = ConfigDialog.INSTANCE; // shorthand...
        if (oldConfiguration == null) { // if nothing passed in, build a new one
            I.dialog.setTitle("Build new machine");
            I.nameField.setText("");
            I.oscBox.getSelectionModel().clearAndSelect(0); // set a default
            I.powBox.getSelectionModel().clearAndSelect(0); // otherwise nothing is selected
        } else {
            I.dialog.setTitle("Edit machine");
            I.nameField.setText(oldConfiguration.name);
            I.oscBox.getSelectionModel().clearAndSelect(oldConfiguration.oscillator.ordinal());
            I.powBox.getSelectionModel().clearAndSelect(oldConfiguration.power.ordinal());
        }

        Platform.runLater(() -> I.nameField.requestFocus());
        
        var builder = new ConfigurationBuilder();
        builder.nameProperty.bind(I.nameField.textProperty());
        builder.oscillatorProperty.bind(I.oscBox.valueProperty());
        builder.powerProperty.bind(I.powBox.valueProperty());

        I.dialog.setResultConverter(type -> {
            if (type == ButtonType.APPLY) {
                Logger.debug("Storing Configuration name {} with Oscillator {} and Power {}",
                    builder.nameProperty.get(), builder.oscillatorProperty.get(), builder.powerProperty.get());
                return builder.makeFrom();
            }
            Logger.debug("Looks like user cancelled Configuration dialog...");
            return null;
        });

        return I.dialog.showAndWait();
    }

    private Dialogs() {} // class only exists for its static methods
}
