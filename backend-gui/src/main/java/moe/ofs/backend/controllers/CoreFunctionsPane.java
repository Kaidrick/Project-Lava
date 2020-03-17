package moe.ofs.backend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.util.ConnectionManager;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class CoreFunctionsPane implements Initializable {

    @FXML
    private Button buttonCoreConfigPortSave;

    @FXML
    private Button buttonCoreConfigPortDefault;

    @FXML
    private TextField portConfigExportQuery;

    @FXML
    private TextField portConfigExportData;

    @FXML
    private TextField portConfigServerQuery;

    @FXML
    private TextField portConfigServerData;

    private final UnaryOperator<TextFormatter.Change> formatter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("([1-9][0-9]*)?")) {
            return change;
        }
        return null;
    };

    @FXML
    public void savePortConfig() {
        Map<Level, Integer> map = ConnectionManager.getInstance().getPortOverrideMap();

        if(!portConfigExportData.getText().equals("") && !portConfigExportQuery.getText().equals("") &&
                !portConfigServerData.getText().equals("") && !portConfigServerQuery.getText().equals("")) {
            map.put(Level.EXPORT_POLL, Integer.valueOf(portConfigExportData.getText()));
            map.put(Level.EXPORT, Integer.valueOf(portConfigExportQuery.getText()));
            map.put(Level.SERVER_POLL, Integer.valueOf(portConfigServerData.getText()));
            map.put(Level.SERVER, Integer.valueOf(portConfigServerQuery.getText()));

            Map<String, String> configMap = map.entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().name(),
                            entry -> String.valueOf(entry.getValue())));

            ConnectionManager.getInstance().writeConfiguration(configMap);
        }
    }

    @FXML
    public void defaultPortConfig() {
        portConfigServerQuery.clear();
        portConfigServerData.clear();
        portConfigExportQuery.clear();
        portConfigExportData.clear();

        Map<Level, Integer> map = ConnectionManager.getInstance().getPortOverrideMap();
        map.put(Level.EXPORT_POLL, Level.EXPORT_POLL.getPort());
        map.put(Level.EXPORT, Level.EXPORT.getPort());
        map.put(Level.SERVER_POLL, Level.SERVER_POLL.getPort());
        map.put(Level.SERVER, Level.SERVER.getPort());

        Map<String, String> configMap = map.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        entry -> String.valueOf(entry.getValue())));

        ConnectionManager.getInstance().writeConfiguration(configMap);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        portConfigExportData.setTextFormatter(new TextFormatter<>(formatter));
        portConfigExportQuery.setTextFormatter(new TextFormatter<>(formatter));
        portConfigServerData.setTextFormatter(new TextFormatter<>(formatter));
        portConfigServerQuery.setTextFormatter(new TextFormatter<>(formatter));
    }
}
