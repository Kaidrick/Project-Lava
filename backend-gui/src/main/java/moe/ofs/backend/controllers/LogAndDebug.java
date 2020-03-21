package moe.ofs.backend.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import jfxtras.styles.jmetro.JMetroStyleClass;
import moe.ofs.backend.gui.LogMessageListViewCell;
import moe.ofs.backend.logmanager.Level;
import moe.ofs.backend.logmanager.LogAppendedEventHandler;
import moe.ofs.backend.logmanager.LogEntry;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.util.LuaScripts;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.ToggleSwitch;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LogAndDebug implements Initializable {

    private List<LogEntry> logEntryList;

    @FXML private AnchorPane baseAnchorPane;
    @FXML private AnchorPane upperAnchorPane;
    @FXML private AnchorPane lowerAnchorPane;
    @FXML private HBox lowerMainHBox;
    @FXML private ListView<LogEntry> listViewLogDebugInfo;
//    @FXML private TextArea textArea_LuaDebugString;
    @FXML private RadioButton radioLoadstringAPI;
    @FXML private RadioButton radioLoadstringState;
    @FXML private ToggleSwitch toggleSwitch_LuaDebugInteractive;

    @FXML private CheckComboBox<Level> levelFilterSelection;

    @FXML private Button scrollToBottom;

    // manual load
    private CodeEditor luaEditor;

    @FXML public void logListViewScrollToBottom() {
        listViewLogDebugInfo.scrollTo(listViewLogDebugInfo.getItems().size());
    }

    @FXML private void appendLog(LogEntry logEntry) {
        logEntryList.add(logEntry);

        // if this level is checked, add to log view
        if(levelFilterSelection.getCheckModel().getCheckedItems().contains(logEntry.getLevel())) {
            listViewLogDebugInfo.getItems().add(logEntry);
        }
    }

    @FXML public void debugLuaString(ActionEvent actionEvent) {
        if (radioLoadstringState.isSelected()) {
            ServerExecRequest serverExecRequest =
                    new ServerExecRequest(luaEditor.readEditorContent());
            serverExecRequest.send();
        } else if (radioLoadstringAPI.isSelected()) {
            ServerExecRequest serverExecRequest =
                    new ServerExecRequest(RequestToServer.State.DEBUG,
                            luaEditor.readEditorContent());
            serverExecRequest.send();
        }
    }

    @FXML public void selectLoadstringApi(ActionEvent actionEvent) {
        radioLoadstringAPI.setSelected(true);
        radioLoadstringState.setSelected(false);

//        radioLoadstringAPI.setSelected(radioLoadstringAPI.isSelected());
    }
    @FXML public void selectLoadstringLuaState(ActionEvent actionEvent) {
        radioLoadstringAPI.setSelected(false);
        radioLoadstringState.setSelected(true);
    }

    @FXML public void reloadCurrentMission(ActionEvent actionEvent) {
        new ServerExecRequest(RequestToServer.State.DEBUG,
                LuaScripts.load("api/reload_current_mission.lua")).send();
    }

    @FXML public void toggleInteractiveLuaDebug(MouseEvent actionEvent) {
        System.out.println(actionEvent);
    }

    @FXML public void clearLuaString(ActionEvent actionEvent) {
        luaEditor.clearEditorContent();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        FXMLLoader loader = new FXMLLoader();
        try {
            AnchorPane topAnchorPanel = loader.load(getClass().getResourceAsStream("/CodeEditor.fxml"));
            luaEditor = loader.getController();
            lowerMainHBox.getChildren().add(0, topAnchorPanel);
            HBox.setHgrow(topAnchorPanel, Priority.ALWAYS);
        } catch (IOException iex) {
            System.out.println("File not found");
        }


        LogAppendedEventHandler handler = this::appendLog;
        handler.attach();

        logEntryList = FXCollections.observableList(new ArrayList<>());

        baseAnchorPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        lowerAnchorPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);

//        System.out.println(textArea_LuaDebugString.getStyleClass());
//        textArea_LuaDebugString.focusedProperty().addListener(((observable, oldValue, newValue) -> {
//            System.out.println(textArea_LuaDebugString.getStylesheets());
//            System.out.println(textArea_LuaDebugString.getStyleClass());
//        }));
        
        listViewLogDebugInfo.setCellFactory(p -> new LogMessageListViewCell());

        upperAnchorPane.setOnMouseEntered(event -> scrollToBottom.setVisible(true));
        upperAnchorPane.setOnMouseExited(event -> scrollToBottom.setVisible(false));

        toggleSwitch_LuaDebugInteractive.getStylesheets().add("toggleswitch.css");

        toggleSwitch_LuaDebugInteractive.getStyleClass().clear();
        toggleSwitch_LuaDebugInteractive.getStyleClass().add("overridden-toggle-switch");

        toggleSwitch_LuaDebugInteractive.selectedProperty()
                .addListener((observable, oldValue, newValue) -> System.out.println("toggled?"));

        levelFilterSelection.getItems().addAll(Level.values());
        levelFilterSelection.getCheckModel().checkAll();

        levelFilterSelection.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super Level>) c -> {
            ObservableList<Level> checkedLevels = levelFilterSelection.getCheckModel().getCheckedItems();

            listViewLogDebugInfo.getItems().clear();

            logEntryList.stream()
                    .filter(logEntry -> checkedLevels.contains(logEntry.getLevel()))
                    .forEach(logEntry -> listViewLogDebugInfo.getItems().add(logEntry));

        });
    }
}
