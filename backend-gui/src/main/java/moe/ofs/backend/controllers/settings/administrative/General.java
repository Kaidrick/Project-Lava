package moe.ofs.backend.controllers.settings.administrative;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.stage.Modality;
import moe.ofs.backend.ControlPanelApplication;
import moe.ofs.backend.function.Message;
import moe.ofs.backend.gui.AddGeneralMessageDialog;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class General implements Initializable {

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

        Optional<String> optional = dialog.showAndWait();
        optional.ifPresent(o -> {
            if(!o.equals(""))
                listViewGeneralInfo.getItems().add(new Message(o));
        });
    }

    @FXML
    private void removeGeneralMessageOnClick(ActionEvent event) {
        if(model.getSelectedIndex() != -1)
            listViewGeneralInfo.getItems().remove(model.getSelectedIndex());
    }

    @FXML
    private void moveUpGeneralMessageOnClick(ActionEvent event) {
        int currentIndex = model.getSelectedIndex();

        if(currentIndex != 0) {
            Message message = model.getSelectedItem();
            listViewGeneralInfo.getItems().remove(currentIndex);
            listViewGeneralInfo.getItems().add(currentIndex - 1, message);

            model.select(currentIndex - 1);
        }
    }

    @FXML
    private void moveDownGeneralMessageOnClick(ActionEvent event) {
        int currentIndex = model.getSelectedIndex();

        if(currentIndex < listViewGeneralInfo.getItems().size() - 1) {
            Message message = model.getSelectedItem();
            listViewGeneralInfo.getItems().remove(currentIndex);
            listViewGeneralInfo.getItems().add(currentIndex + 1, message);

            model.select(currentIndex + 1);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        listViewGeneralInfo.getItems().add(new Message("what is this?"));
        model = listViewGeneralInfo.getSelectionModel();
    }
}
