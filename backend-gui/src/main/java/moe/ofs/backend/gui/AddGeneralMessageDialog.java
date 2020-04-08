package moe.ofs.backend.gui;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import moe.ofs.backend.function.triggermessage.Message;

import java.util.Locale;
import java.util.function.UnaryOperator;

public class AddGeneralMessageDialog extends Dialog<Message> {

    private DialogPane dialogPane;
    private TextArea message = new TextArea();
    private TextField duration = new TextField();
    private ComboBox<Locale> localeComboBox = new ComboBox<>();

    public AddGeneralMessageDialog(Message message) {
        this();

        this.message.setText(message.getContent());
        this.duration.setText(String.valueOf(message.getDuration()));
    }

    public AddGeneralMessageDialog() {

        dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label descLabel = new Label("Enter a new message:");
        message.setPromptText("Type message here");

        Label durationLabel = new Label("Duration(in seconds)");

        UnaryOperator<TextFormatter.Change> formatter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([1-9][0-9]*)?")) {
                return change;
            }
            return null;
        };
        duration.setTextFormatter(new TextFormatter<>(formatter));

        HBox hBox = new HBox(10);
        VBox vBox = new VBox(10);

        hBox.getChildren().addAll(duration, localeComboBox);

        vBox.getChildren().addAll(descLabel, message, durationLabel, hBox);

        dialogPane.setContent(vBox);

        setResultConverter(b -> b.equals(ButtonType.OK) ?
                new Message(message.getText(), Integer.parseInt(duration.getText())) : null);

        Platform.runLater(() -> message.requestFocus());
    }
}
