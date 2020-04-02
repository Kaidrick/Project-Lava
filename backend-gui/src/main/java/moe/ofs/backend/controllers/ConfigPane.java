package moe.ofs.backend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import moe.ofs.backend.util.DcsScriptConfigManager;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

@Component
@FxmlView
public class ConfigPane implements Initializable {

    private Path selectedBranchPath;
    @FXML
    private ComboBox<Path> comboBoxDcsBranchSelection;
    @FXML private Button buttonExportAndHookConfig;
    @FXML private Button buttonRemoveBackendConfigFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // populate dcs branch combobox
        DcsScriptConfigManager manager = new DcsScriptConfigManager();
        comboBoxDcsBranchSelection.setItems(manager.getUserDcsWritePaths());
        comboBoxDcsBranchSelection.getSelectionModel().selectedItemProperty().addListener
                ((observable, oldValue, newValue) -> {
                    if(newValue != null) {
                        buttonExportAndHookConfig.setDisable(false);
                        // check if correctly configured
                        if(manager.isInjectionConfigured(newValue.getFileName())) {
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
            if(manager.isInjectionConfigured(branch)) {  // if configured, remove
                manager.removeInjection(branch);
            } else {  // if not configured, install
                manager.injectIntoHooks(branch);
                manager.injectIntoExport(branch);
            }
            // reset button text based on whether scripts are configured
            if(manager.isInjectionConfigured(branch)) {
                buttonExportAndHookConfig.setText("Uninstall Scripts");
            } else {
                buttonExportAndHookConfig.setText("Install Scripts");
            }
        });

    }
}
