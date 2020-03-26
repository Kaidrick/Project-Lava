package moe.ofs.backend.gui;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import moe.ofs.backend.logmanager.LogEntry;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class LogMessageListViewCell extends ListCell<LogEntry> {
    private final HBox hBox;
    private final Label time;
    private final Label level;
    private final Label message;

    private ContextMenu contextMenu;

    public LogMessageListViewCell(ListView<LogEntry> listView) {
        super();

        hBox = new HBox();
        time = new Label();
        time.setPrefWidth(140);

        level = new Label();
        level.setPrefWidth(50);

        message = new Label();

        hBox.getChildren().addAll(time, level, message);

        setOnMouseClicked(event -> {
            if(event.getClickCount() >= 2 && event.getButton() == MouseButton.PRIMARY) {
                new LogEntryDetailDialog(getItem()).show();
            }
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyLogText = new MenuItem();
        copyLogText.setText("Copy selected log message(s)");
        copyLogText.setOnAction(event -> {
            ClipboardContent content = new ClipboardContent();
            String messages = listView.getSelectionModel().getSelectedItems().stream()
                    .map(LogEntry::toString)
                    .collect(Collectors.joining("\n"));
            content.putString(messages);
            Clipboard.getSystemClipboard().setContent(content);
        });
        contextMenu.getItems().setAll(copyLogText);
        setContextMenu(contextMenu);
    }

    @Override
    protected void updateItem(LogEntry item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if(item != null && !empty) {
            time.setText(item.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            level.getStyleClass().clear();
            level.getStyleClass().add(item.getLevel().getStyle());

            level.setText(item.getLevel().name());
            level.getStyleClass().clear();
            level.getStyleClass().add(item.getLevel().getStyle());
//            level.setStyle("-fx-font-weight: bold;");

            message.setText(item.getMessage());

            selectedProperty().addListener(((observable, oldValue, newValue) -> {
                if(oldValue != newValue) {
                    level.getStyleClass().clear();

                    if(observable.getValue() && newValue) {
                        level.getStyleClass().add("log-message-selected");
                    } else {
                        level.getStyleClass().add(item.getLevel().getStyle());
                    }
                }
            }));

            setGraphic(hBox);
        }
    }
}
