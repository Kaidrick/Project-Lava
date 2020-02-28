package moe.ofs.backend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import jfxtras.styles.jmetro.JMetroStyleClass;
import moe.ofs.backend.gui.PluginListCell;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.util.AirdromeDataCollector;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    private ResourceBundle bundle;
    @FXML private AnchorPane anchorPane;
    @FXML private Button button_ClearLuaString;
    @FXML private Button mainLoopStartButton;
    @FXML private TextArea logTextArea;
    @FXML private Button button_ExecuteLuaDebug;
    @FXML private TextArea textArea_LuaDebugString;
    @FXML private RadioButton radioLoadstringAPI;
    @FXML private RadioButton radioLoadstringState;
    @FXML private Label labelConnectionStatus;
    @FXML private ToggleSwitch toggleSwitch_LuaDebugInteractive;
    @FXML private StatusBar statusBar_Connection;
    @FXML private ListView<String> listViewAddons;

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

    @FXML public void superTestButtonVeryDangerous(ActionEvent actionEvent) {
        PluginClassLoader.loadedPluginSet.forEach(p -> p.getPluginListCell().getControlButton().setText("trololo"));
    }

    @FXML public void toggleInteractiveLuaDebug(MouseEvent actionEvent) {
        System.out.println(actionEvent);
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

    @FXML public void setConnectionStatusBarText(String status) {
        statusBar_Connection.setText(status);
    }

    @FXML public void populateLoadedPluginListView() {
        Set<Plugin> pluginSet = new HashSet<>(PluginClassLoader.loadedPluginSet);
        List<String> pluginNameList = pluginSet.stream().map(Plugin::getName).collect(Collectors.toList());

        ObservableList<String> list = FXCollections.observableArrayList(pluginNameList);

        listViewAddons.setItems(list);
        listViewAddons.setCellFactory(param -> new PluginListCell());

        System.out.println("populateLoadedPluginListView");
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;

        anchorPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        toggleSwitch_LuaDebugInteractive.selectedProperty()
                .addListener((observable, oldValue, newValue) -> System.out.println("toggled?"));
    }
}
