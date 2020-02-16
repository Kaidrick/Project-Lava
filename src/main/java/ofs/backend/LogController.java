package ofs.backend;

import javafx.scene.control.*;
import ofs.backend.core.request.RequestToServer;
import ofs.backend.core.request.server.ServerExecRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;

import ofs.backend.util.AirdromeDataCollector;

import java.net.URL;
import java.util.ResourceBundle;

public class LogController implements Initializable {

    private ResourceBundle bundle;
    @FXML private Button button_ClearLuaString;
    @FXML private Button mainLoopStartButton;
    @FXML private TextArea logTextArea;
    @FXML private Button button_ExecuteLuaDebug;
    @FXML private TextArea textArea_LuaDebugString;
    @FXML private RadioButton radioLoadstringAPI;
    @FXML private RadioButton radioLoadstringState;
    @FXML private CheckBox checkBox_LuaDebugInteractive;
    @FXML private Label labelConnectionStatus;

    @FXML public void appendLog(String logMessage) {
        logTextArea.appendText(logMessage);
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

    @FXML public void setLabelConnectionStatus(String status) {
        labelConnectionStatus.setText(status);
    }

    @FXML public void clearLuaString(ActionEvent actionEvent) {
        textArea_LuaDebugString.clear();
    }

    @FXML public void testStart(ActionEvent actionEvent) {
        AirdromeDataCollector.collect();
    }

    @FXML public void selectLoadstringApi(ActionEvent actionEvent) {
        radioLoadstringAPI.setSelected(true);
        radioLoadstringState.setSelected(false);
    }
    @FXML public void selectLoadstringLuaState(ActionEvent actionEvent) {
        radioLoadstringAPI.setSelected(false);
        radioLoadstringState.setSelected(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
    }
}
