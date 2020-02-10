package main;

import com.google.gson.Gson;
import core.Logger;
import core.LuaScripts;
import core.object.Group;
import core.object.Unit;
import core.request.server.ServerDataRequest;
import core.request.server.ServerExecRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import javafx.scene.control.Button;
import plugin.static_display.AirdromeDataCollector;

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

    @FXML public void testStart(ActionEvent actionEvent) {
        System.out.println(actionEvent.toString());

        AirdromeDataCollector.collect();
    }

    @FXML public void selectLoadstringApi(ActionEvent actionEvent) {
        System.out.println(actionEvent);
    }
    @FXML public void selectLoadstringLuaState(ActionEvent actionEvent) {
        System.out.println(actionEvent);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
    }
}
