package us.malfeasant.admiral64.configuration;

import java.util.Optional;
import org.tinylog.Logger;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;

public class Dialogs {
    public static Optional<Configuration> showCreateDialog() {
        var dialog = new Dialog<Configuration>();
        dialog.setTitle("Build new machine");
        var createType = new ButtonType("Create", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createType, ButtonType.CANCEL);
        var pane = new GridPane();
        var nameField = new TextField();
        Platform.runLater(() -> nameField.requestFocus());
        nameField.setPromptText("Enter machine name:");
        pane.add(nameField, 0, 0);

        var oscBox = new ChoiceBox<Oscillator>();
        oscBox.getItems().addAll(Oscillator.values());
        oscBox.getSelectionModel().clearAndSelect(0);
        pane.add(oscBox, 1, 0);

        var powBox = new ChoiceBox<Power>();
        powBox.getItems().addAll(Power.values());
        powBox.getSelectionModel().clearAndSelect(0);
        pane.add(powBox, 2, 0);

        var builder = new ConfigurationBuilder();
        builder.nameProperty.bind(nameField.textProperty());
        builder.oscillatorProperty.bind(oscBox.valueProperty());
        builder.powerProperty.bind(powBox.valueProperty());

        dialog.getDialogPane().setContent(pane);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == createType) {
                Logger.debug("Creating new machine named " + builder.nameProperty.get() +
                    " with Oscillator " + builder.oscillatorProperty.get() +
                    " and Power " + builder.powerProperty.get());
                return builder.makeFrom();
            }
            Logger.debug("Looks like user cancelled machine creation...");
            return null;
        });
        return dialog.showAndWait();
    }

    private Dialogs() {} // only exists for static methods
}
