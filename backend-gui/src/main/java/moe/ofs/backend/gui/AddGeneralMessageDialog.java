package moe.ofs.backend.gui;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Locale;

public class AddGeneralMessageDialog extends Dialog<String> {

    private DialogPane dialogPane;
    private TextArea textArea = new TextArea();
    private ComboBox<Locale> localeComboBox = new ComboBox<>();

    public AddGeneralMessageDialog() {
        dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label descLabel = new Label("Enter a new message:");
        textArea.setPromptText("Type in message here...");

        VBox vBox = new VBox(10);

        vBox.getChildren().addAll(descLabel, textArea, localeComboBox);

        dialogPane.setContent(vBox);

        setResultConverter(b -> b.equals(ButtonType.OK) ?
                textArea.getText() : null);

        Platform.runLater(() -> textArea.requestFocus());
    }
}
