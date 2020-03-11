package moe.ofs.backend.gui;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.function.UnaryOperator;

public class BanPlayerOptionDialog extends Dialog<BanPlayerOptionDialogResult> {

    private static final DecimalFormat format = new DecimalFormat("#");
    private final UnaryOperator<TextFormatter.Change> formatter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("([1-9][0-9]*)?")) {
            switchValidateOnCondition();
            return change;
        }
        return null;
    };

    private DialogPane dialogPane;
    private TextField reasonTextField = new TextField();
    private TextField daysTextField = new TextField();
    private TextField hoursTextField = new TextField();
    private TextField minutesTextField = new TextField();

    private LocalDate localDate = LocalDate.now();

    private DatePicker datePicker = new DatePicker(localDate);
    private CheckBox permanentCheckBox = new CheckBox();

    public BanPlayerOptionDialog() {
        dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label reasonLabel = new Label("for");
        reasonTextField.setPromptText("Specify a reason to ban");

        Label datePickerLabel = new Label("until");

        datePicker.valueProperty().addListener(((observable, oldValue, newValue) ->
                {
                    switchDurationFieldAvailabilityOnCondition();
                    switchValidateOnCondition();
                }));

        Button datePickerClearButton = new Button("Reset");
        datePickerClearButton.setOnAction(event -> datePicker.setValue(localDate));

        HBox datePickerHBox = new HBox(5);
        datePickerHBox.getChildren().addAll(datePicker, datePickerClearButton);

        Label durationLabel = new Label("or lift ban after");

        daysTextField.setPromptText("days");
        daysTextField.setTextFormatter(new TextFormatter<>(formatter));
        hoursTextField.setPromptText("hours");
        hoursTextField.setTextFormatter(new TextFormatter<>(formatter));
        minutesTextField.setPromptText("minutes");
        minutesTextField.setTextFormatter(new TextFormatter<>(formatter));

        HBox durationFieldsHBox = new HBox();

        durationFieldsHBox.setSpacing(5);
        durationFieldsHBox.getChildren().addAll(daysTextField, hoursTextField, minutesTextField);

        permanentCheckBox.setText("Permanent Ban");
        permanentCheckBox.setSelected(false);
        permanentCheckBox.setOnAction(event -> {
            if(datePicker.isDisabled()) {
                datePicker.setDisable(false);
            } else {
                datePicker.setDisable(true);
            }
            switchDurationFieldAvailabilityOnCondition();
            switchValidateOnCondition();
        });

        dialogPane.setContent(new VBox(10, reasonLabel, reasonTextField, datePickerLabel,
                datePickerHBox, durationLabel, durationFieldsHBox, permanentCheckBox));

        Platform.runLater(reasonTextField::requestFocus);

        setResultConverter(b -> b.equals(ButtonType.OK) ?
                new BanPlayerOptionDialogResult(reasonTextField.getText(),
                        daysTextField.getText(), hoursTextField.getText(), minutesTextField.getText(),
                        permanentCheckBox.isSelected(),
                        datePicker.getValue()) : null);
    }

    private void switchDurationFieldAvailabilityOnCondition() {
        if(!datePicker.getValue().equals(localDate)) {
            daysTextField.setDisable(true);
            hoursTextField.setDisable(true);
            minutesTextField.setDisable(true);
        } else {
            daysTextField.setDisable(false);
            hoursTextField.setDisable(false);
            minutesTextField.setDisable(false);
        }

        if(permanentCheckBox.isSelected()) {
            daysTextField.setDisable(true);
            hoursTextField.setDisable(true);
            minutesTextField.setDisable(true);
        }
    }

    private void switchValidateOnCondition() {
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        if(!daysTextField.getText().equals("") ||
                !hoursTextField.getText().equals("") ||
                !minutesTextField.getText().equals("") ||
                !datePicker.getValue().equals(localDate) ||
                permanentCheckBox.isSelected()) {
            okButton.setDisable(false);
        } else {
            okButton.setDisable(true);
        }

    }
}
