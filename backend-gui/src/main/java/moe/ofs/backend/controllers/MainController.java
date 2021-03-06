package moe.ofs.backend.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;
import moe.ofs.backend.Plugin;
import moe.ofs.backend.UTF8Control;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.gui.PlayerListCellFactory;
import moe.ofs.backend.gui.PluginListCell;
import moe.ofs.backend.handlers.BackgroundTaskRestartObservable;
import moe.ofs.backend.handlers.PlayerEnterServerObservable;
import moe.ofs.backend.handlers.PlayerLeaveServerObservable;
import moe.ofs.backend.interaction.TestButtonCommand;
import moe.ofs.backend.request.RequestHandler;
import moe.ofs.backend.util.AirdromeDataCollector;
import moe.ofs.backend.util.ConnectionManager;
import moe.ofs.backend.util.I18n;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.StatusBar;
import org.springframework.stereotype.Component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
@FxmlView("ControlPanelApplication.fxml")
public class MainController implements Initializable, PropertyChangeListener {

    private ResourceBundle bundle;

    @FXML private VBox mainVBox;
    @FXML private TabPane baseTabPane;

    @FXML private Tab consoleTab;
    @FXML private Tab settingTab;
    @FXML private Tab devTestTab;
    @FXML private Tab playerTab;
    @FXML private Tab luaEditorTab;

    @FXML private StatusBar statusBar_Connection;
    @FXML private ListView<Plugin> listViewAddons;
    @FXML private ListView<String> listViewConnectedPlayer;

    @FXML private Label labelDebugInfo1;
    @FXML private Label labelDebugInfo2;

    @FXML private Button devTestButton;

    private final PlayerListCellFactory playerListCellFactory;

    private long pileCount;
    private long previousTimeStamp = Instant.now().toEpochMilli();

    public MainController(PlayerListCellFactory playerListCellFactory) {
        this.playerListCellFactory = playerListCellFactory;
    }


    @FXML public void setDebugLabelTextOne(String info) {
        labelDebugInfo1.setText(info);
    }
    @FXML public void setDebugLabelTextTwo(String info) {
        labelDebugInfo2.setText(info);
    }

    @FXML public void testStart(ActionEvent actionEvent) {
        AirdromeDataCollector.collect();
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
        System.out.println("populateLoadedPluginListView");

//        Set<Plugin> pluginSet = new HashSet<>(PluginClassLoader.loadedPluginSet);

        Set<Plugin> plugins = new HashSet<>(Plugin.loadedPlugins);

        ObservableList<Plugin> list = FXCollections.observableArrayList(
                plugins.stream()
                        .sorted(Comparator.comparingInt(p ->
                                p.getName().length())).collect(Collectors.toList()));

        listViewAddons.setItems(list);
        listViewAddons.setCellFactory(PluginListCell::new);
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

        RequestHandler.getInstance().addPropertyChangeListener(this);

        baseTabPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        PlayerEnterServerObservable playerEnterServerObservable = this::addPlayerToListView;
        playerEnterServerObservable.register();

        PlayerLeaveServerObservable playerLeaveServerObservable = this::removePlayerFromListView;
        playerLeaveServerObservable.register();

        BackgroundTaskRestartObservable backgroundTaskRestartObservable = this::removeAllPlayerFromListView;
        backgroundTaskRestartObservable.register();


        listViewConnectedPlayer.setCellFactory(lv -> playerListCellFactory.listView(lv).getObject());
        populateLoadedPluginListView();


        // TEST
        devTestButton.setOnAction(event -> TestButtonCommand.invokeAll());

        I18n.localeProperty().addListener(((observable, oldValue, newValue) -> {
            ResourceBundle bundle =
                    ResourceBundle.getBundle("ControlPanelApplication", newValue, new UTF8Control());

            I18n.toPaneOrNotToPane(mainVBox, bundle);
//            parsePane(mainVBox, bundle);
        }));

        ConnectionManager.connectionCountProperty().addListener(((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if(Instant.now().toEpochMilli() - previousTimeStamp > 5000) {
                    previousTimeStamp = Instant.now().toEpochMilli();
                    setDebugLabelTextTwo("Connection Request per seconds: " + pileCount / 5);

                    pileCount = 0L;
                } else {  // elapsed time less than 1000ms
                    pileCount += (newValue - oldValue);
                }

                setDebugLabelTextOne("Total connection request made: " + newValue);
            });
        }));

    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("trouble")) {
            connectionStatusBarTextSwitch((boolean) propertyChangeEvent.getNewValue());
        }
    }
}
