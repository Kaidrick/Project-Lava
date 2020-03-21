package moe.ofs.backend.gui;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import moe.ofs.backend.logmanager.LogEntry;

import java.time.format.DateTimeFormatter;

public class LogMessageListViewCell extends ListCell<LogEntry> {
    private final HBox hBox;
    private final Label time;
    private final Label level;
    private final Label message;

    private ContextMenu contextMenu;

    public LogMessageListViewCell() {
        super();

        hBox = new HBox();
        time = new Label();
        time.setPrefWidth(140);

        level = new Label();
        level.setPrefWidth(50);

        message = new Label();

        hBox.getChildren().addAll(time, level, message);
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
