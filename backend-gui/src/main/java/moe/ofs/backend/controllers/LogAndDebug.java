package moe.ofs.backend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import moe.ofs.backend.logmanager.LogAppendedEventHandler;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.util.LuaScripts;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.util.ResourceBundle;

public class LogAndDebug implements Initializable {

    @FXML private ListView<String> listViewLogDebugInfo;
    @FXML private TextArea textArea_LuaDebugString;
    @FXML private RadioButton radioLoadstringAPI;
    @FXML private RadioButton radioLoadstringState;
    @FXML private ToggleSwitch toggleSwitch_LuaDebugInteractive;

    @FXML private void appendLog(String logMessage) {
        listViewLogDebugInfo.getItems().add(logMessage);
    }

    @FXML public void debugLuaString(ActionEvent actionEvent) {
        if (radioLoadstringState.isSelected()) {
            ServerExecRequest serverExecRequest =
                    new ServerExecRequest(textArea_LuaDebugString.getText());
            System.out.println(textArea_LuaDebugString.getText());

            serverExecRequest.send();
        } else if (radioLoadstringAPI.isSelected()) {
            ServerExecRequest serverExecRequest =
                    new ServerExecRequest(RequestToServer.State.DEBUG,
                            textArea_LuaDebugString.getText());
            System.out.println(textArea_LuaDebugString.getText());

            serverExecRequest.send();
        }
    }

    @FXML public void selectLoadstringApi(ActionEvent actionEvent) {
        radioLoadstringAPI.setSelected(true);
        radioLoadstringState.setSelected(false);
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
        textArea_LuaDebugString.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LogAppendedEventHandler handler = this::appendLog;
        handler.attach();

        toggleSwitch_LuaDebugInteractive.selectedProperty()
                .addListener((observable, oldValue, newValue) -> System.out.println("toggled?"));
    }
}
