package main;

import core.request.server.ServerExecRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class LogController implements Initializable {

    private ResourceBundle bundle;
    @FXML private Button button_ClearLuaString;
    @FXML private Button mainLoopStartButton;
    @FXML private TextArea logTextArea;
    @FXML private Button button_ExecuteLuaDebug;
    @FXML private TextArea textArea_LuaDebugString;

    @FXML public void appendLog(String logMessage) {
        logTextArea.appendText(logMessage);
    }

    @FXML public void debugLuaString(ActionEvent actionEvent) {
        ServerExecRequest serverExecRequest =
                new ServerExecRequest(textArea_LuaDebugString.getText());
        System.out.println(textArea_LuaDebugString.getText());

        serverExecRequest.send();
    }

    @FXML public void clearLuaString(ActionEvent actionEvent) {
        textArea_LuaDebugString.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
    }
}
