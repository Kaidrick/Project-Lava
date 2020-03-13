package moe.ofs.backend;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import jfxtras.styles.jmetro.JMetroStyleClass;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.gui.PlayerListCellFactory;
import moe.ofs.backend.gui.PluginListCell;
import moe.ofs.backend.handlers.BackgroundTaskRestartObservable;
import moe.ofs.backend.handlers.PlayerEnterServerObservable;
import moe.ofs.backend.handlers.PlayerLeaveServerObservable;
import moe.ofs.backend.logmanager.LogAppendedEventHandler;
import moe.ofs.backend.request.RequestHandler;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.util.AirdromeDataCollector;
import moe.ofs.backend.util.DcsScriptConfigManager;
import moe.ofs.backend.util.LuaScripts;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.ToggleSwitch;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class MainController implements Initializable, PropertyChangeListener {

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
    @FXML private ListView<String> listViewConnectedPlayer;

    @FXML private Label labelDebugInfo1;
    @FXML private Label labelDebugInfo2;

    private Path selectedBranchPath;
    @FXML private ComboBox<Path> comboBoxDcsBranchSelection;
    @FXML private Button buttonExportAndHookConfig;
    @FXML private Button buttonRemoveBackendConfigFile;

    private BackgroundTask backgroundTask;

    @FXML public void setDebugLabelTextOne(String info) {
        labelDebugInfo1.setText(info);
    }
    @FXML public void setDebugLabelTextTwo(String info) {
        labelDebugInfo2.setText(info);
    }

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

    @FXML public void reloadCurrentMission(ActionEvent actionEvent) {
//        PluginClassLoader.loadedPluginSet.forEach(p -> p.getPluginListCell().getControlButton().setText("trololo"));

        new ServerExecRequest(RequestToServer.State.DEBUG,
                LuaScripts.load("api/reload_current_mission.lua")).send();
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

    // listen to property change of "started" in BackgroundTask
    @FXML public void connectionStatusBarTextSwitch(boolean trouble) {
        if(!trouble) {
            Platform.runLater(() -> statusBar_Connection.setText("Connected"));
        } else {
            Platform.runLater(() -> statusBar_Connection.setText("Waiting for connection..."));
        }
    }

    // should be populated on application start, and there is no need to modify the list after start
    @FXML public void populateLoadedPluginListView() {
        Set<Plugin> pluginSet = new HashSet<>(PluginClassLoader.loadedPluginSet);
        List<String> pluginNameList = pluginSet.stream().map(Plugin::getName).collect(Collectors.toList());

        ObservableList<String> list = FXCollections.observableArrayList(pluginNameList);

        listViewAddons.setItems(list);
        listViewAddons.setCellFactory(param -> new PluginListCell());

        System.out.println("populateLoadedPluginListView");
    }

    // registered to PlayerEnterServerObservable
    @FXML public void addPlayerToListView(PlayerInfo playerInfo) {
//        if(playerInfo.getId() != 1)
        Platform.runLater(() -> listViewConnectedPlayer.getItems().addAll(playerInfo.getName()));
    }

    @FXML public void removePlayerFromListView(PlayerInfo playerInfo) {
//        if(playerInfo.getId() != 1)
        Platform.runLater(() -> listViewConnectedPlayer.getItems().remove(playerInfo.getName()));
    }

    @FXML public void removeAllPlayerFromListView() {
        listViewConnectedPlayer.getItems().forEach(item ->
                Platform.runLater(() -> listViewConnectedPlayer.getItems().remove(item)));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;

//        backgroundTask =  ControlPanelApplication.applicationContext.getBean(BackgroundTask.class);
//        backgroundTask.addPropertyChangeListener(this);

        RequestHandler.getInstance().addPropertyChangeListener(this);

        // attach appendLog() to log append event handler
        LogAppendedEventHandler handler = this::appendLog;
        handler.attach();

        anchorPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        toggleSwitch_LuaDebugInteractive.selectedProperty()
                .addListener((observable, oldValue, newValue) -> System.out.println("toggled?"));

        PlayerEnterServerObservable playerEnterServerObservable = this::addPlayerToListView;
        playerEnterServerObservable.register();

        PlayerLeaveServerObservable playerLeaveServerObservable = this::removePlayerFromListView;
        playerLeaveServerObservable.register();

        BackgroundTaskRestartObservable backgroundTaskRestartObservable = this::removeAllPlayerFromListView;
        backgroundTaskRestartObservable.register();

        PlayerListCellFactory factory =
                ControlPanelApplication.applicationContext.getBean(PlayerListCellFactory.class);

        listViewConnectedPlayer.setCellFactory(lv -> factory.listView(lv).getObject());

        populateLoadedPluginListView();

        // populate dcs branch combobox
        DcsScriptConfigManager manager = new DcsScriptConfigManager();
        comboBoxDcsBranchSelection.setItems(manager.getUserDcsWritePaths());
        comboBoxDcsBranchSelection.getSelectionModel().selectedItemProperty().addListener
                ((observable, oldValue, newValue) -> {
                    if(newValue != null) {
                        buttonExportAndHookConfig.setDisable(false);
                        // check if correctly configured
                        if(manager.injectionConfigured(newValue.getFileName())) {
                            buttonExportAndHookConfig.setText("Uninstall Scripts");
                        } else {
                            buttonExportAndHookConfig.setText("Install Scripts");
                        }
                    }
                });

        // config button
        buttonExportAndHookConfig.setOnAction(event -> {
            // get combobox selected item
            Path branch = comboBoxDcsBranchSelection.getValue().getFileName();
            System.out.println(branch.toString());
            if(manager.injectionConfigured(branch)) {  // if configured, remove
                manager.removeInjection(branch);
            } else {  // if not configured, install
                manager.injectIntoHooks(branch);
                manager.injectIntoExport(branch);
            }
            // reset button text based on whether scripts are configured
            if(manager.injectionConfigured(branch)) {
                buttonExportAndHookConfig.setText("Uninstall Scripts");
            } else {
                buttonExportAndHookConfig.setText("Install Scripts");
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("trouble")) {
            connectionStatusBarTextSwitch((boolean) propertyChangeEvent.getNewValue());
        }
    }
}
