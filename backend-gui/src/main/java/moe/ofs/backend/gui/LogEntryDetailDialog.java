package moe.ofs.backend.gui;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import moe.ofs.backend.ControlPanelApplication;
import moe.ofs.backend.logmanager.LogEntry;

public class LogEntryDetailDialog extends Dialog<LogEntry> {
    private DialogPane dialogPane;

    private LogEntry logEntry;

    private TextArea textArea = new TextArea();

    public LogEntryDetailDialog(LogEntry logEntry) {
        this.logEntry = logEntry;

        dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK);

        textArea.setText(logEntry.getMessage());

        textArea.setPrefHeight(300);
        textArea.setPrefWidth(600);

        dialogPane.setContent(textArea);

        initModality(Modality.APPLICATION_MODAL);
        initOwner(ControlPanelApplication.stage);
    }
}
