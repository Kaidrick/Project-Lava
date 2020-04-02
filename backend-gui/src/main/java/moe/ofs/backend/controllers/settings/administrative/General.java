package moe.ofs.backend.controllers.settings.administrative;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.stage.Modality;
import moe.ofs.backend.Configurable;
import moe.ofs.backend.ControlPanelApplication;
import moe.ofs.backend.function.Message;
import moe.ofs.backend.gui.AddGeneralMessageDialog;
import moe.ofs.backend.plugin.greeting.Greeting;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@FxmlView
public class General implements Initializable, Configurable {

    @FXML
    private Button buttonGeneralAdd;

    @FXML
    private Button buttonGeneralRemove;

    @FXML
    private Button buttonGeneralMoveUp;

    @FXML
    private Button buttonGeneralMoveDown;

    @FXML
    private ListView<Message> listViewGeneralInfo;


    private SelectionModel<Message> model;

    /**
     * Open a new dialog on click, and add new message to list view if and only if message is not an empty string.
     * @param event
     */

    @FXML
    private void addGeneralMessageOnClick(ActionEvent event) {
        // prompt new dialog and get result then add item to ListView
        AddGeneralMessageDialog dialog = new AddGeneralMessageDialog();
        dialog.setTitle("Add new message");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(ControlPanelApplication.stage);

        Optional<Message> optional = dialog.showAndWait();
        optional.ifPresent(message -> listViewGeneralInfo.getItems().add(message));

        pushUpdates();
    }

    @FXML
    private void removeGeneralMessageOnClick(ActionEvent event) {
        if(model.getSelectedIndex() != -1)
            listViewGeneralInfo.getItems().remove(model.getSelectedIndex());

        pushUpdates();
    }

    @FXML
    private void moveUpGeneralMessageOnClick(ActionEvent event) {
        int currentIndex = model.getSelectedIndex();

        if(currentIndex > 0) {
            Message message = model.getSelectedItem();
            listViewGeneralInfo.getItems().remove(currentIndex);
            listViewGeneralInfo.getItems().add(currentIndex - 1, message);

            model.select(currentIndex - 1);
        }

        pushUpdates();
    }

    @FXML
    private void moveDownGeneralMessageOnClick(ActionEvent event) {
        int currentIndex = model.getSelectedIndex();

        if(currentIndex > -1 && currentIndex < listViewGeneralInfo.getItems().size() - 1) {
            Message message = model.getSelectedItem();
            listViewGeneralInfo.getItems().remove(currentIndex);
            listViewGeneralInfo.getItems().add(currentIndex + 1, message);

            model.select(currentIndex + 1);
        }

        pushUpdates();
    }

    @FXML
    private void editGeneralMessageOnClick(ActionEvent event) {
        int currentIndex = model.getSelectedIndex();
        if(currentIndex != -1) {
            AddGeneralMessageDialog dialog = new AddGeneralMessageDialog(model.getSelectedItem());

            dialog.setTitle("Edit new message");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(ControlPanelApplication.stage);

            Optional<Message> optional = dialog.showAndWait();
            optional.ifPresent(message -> listViewGeneralInfo.getItems().set(currentIndex, message));

            pushUpdates();
        }
    }


    /**
     * initialize selection model field
     * load data file if it exists
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = listViewGeneralInfo.getSelectionModel();

        if(dataFileExists()) {
            List<Message> messageList = readFile();
            listViewGeneralInfo.getItems().addAll(FXCollections.observableArrayList(messageList));
        }
    }


    // push update after each button click is finished
    private void pushUpdates() {
//        ControlPanelApplication.applicationContext.getBean(Greeting.class).setList(listViewGeneralInfo.getItems());

        writeFile(new ArrayList<>(listViewGeneralInfo.getItems()));
    }

    @Override
    public String getName() {
        return "administrative";
    }
}
