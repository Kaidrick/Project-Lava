package moe.ofs.backend.gui;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.FlatAlert;
import jfxtras.styles.jmetro.JMetro;
import moe.ofs.backend.BackendMain;
import moe.ofs.backend.box.BoxOfFlyableUnit;
import moe.ofs.backend.box.BoxOfPlayerInfo;
import moe.ofs.backend.object.FlyableUnit;
import moe.ofs.backend.object.PlayerInfo;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.util.LuaScripts;

public class PlayerListCell extends ListCell<String> {
    Stage stage;

    HBox hbox = new HBox();
    Pane pane = new Pane();
    Label playerName = new Label();
    Label playerConnData = new Label();

    ContextMenu contextMenu = new ContextMenu();
    MenuItem queryPlayer = new MenuItem();

    public PlayerListCell(ListView<String> listView) {
//        super();
//        hbox.getChildren().addAll(playerName, pane, playerConnData);
//        HBox.setHgrow(pane, Priority.ALWAYS);
//
//        queryPlayer.textProperty().setValue("do something");
//        queryPlayer.setOnAction(event -> System.out.println(getItem()));
//
//        contextMenu.getItems().addAll(queryPlayer);
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem();
//        editItem.textProperty().bind(Bindings.format("Inspect \"%s\"", this.itemProperty()));
        editItem.setText("Detail");
        editItem.setOnAction(event -> {
            // show a prompt dialog that contains the info for this player
            PlayerInfo playerInfo = BoxOfPlayerInfo.findByName(getItem());
            int playerId = playerInfo.getId();
            String playerUcid = playerInfo.getUcid();
            String playerIpaddr = playerInfo.getIpaddr();
            int playerPing = playerInfo.getPing();
            String playerSlot = playerInfo.getSlot().equals("Observer") ? "Observer" : "#" + playerInfo.getSlot();
            String unitName =
                    playerInfo.getSlot().equals("Observer") ?
                    "" :
                    "[" +
                    BoxOfFlyableUnit.getByUnitId(playerInfo.getSlot()).orElse(new FlyableUnit()).getUnit_name() +
                    "]";

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Detail");
            alert.setHeaderText(getItem());
            alert.setContentText(String.format(
                    "NetID: %d" + "\n" +
                    "UCID: %s" + "\n" +
                    "IP Address: %s" + "\n" +
                    "Ping: %sms" + "\n" +
                    "Slot: %s %s",
                    playerId, playerUcid, playerIpaddr, playerPing,
                    playerSlot, unitName));

            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(BackendMain.stage);

            alert.show();
        });
        MenuItem deleteItem = new MenuItem();
//        deleteItem.textProperty().bind(Bindings.format("Kick \"%s\"", this.itemProperty()));
        deleteItem.setText("Kick");
//        deleteItem.setOnAction(event -> listView.getItems().remove(this.getItem()));
        deleteItem.setOnAction(event -> {
            PlayerInfo playerInfo = BoxOfPlayerInfo.findByName(getItem());

            System.out.println("kick " + playerInfo.getName());
            String luaString = LuaScripts.loadAndPrepare("api/kick_net_player.lua",
                    playerInfo.getId(), "You are kicked");
            new ServerExecRequest(RequestToServer.State.DEBUG, luaString).send();
        });
        contextMenu.getItems().addAll(editItem, deleteItem);

        this.textProperty().bind(this.itemProperty());

        this.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
            if (isNowEmpty) {
                this.setContextMenu(null);
            } else {
                this.setContextMenu(contextMenu);
            }
        });
    }
//    @Override
//    protected void updateItem(String item, boolean empty) {
//        super.updateItem(item, empty);
//        setText(null);
//        setGraphic(null);
//
//        if (item != null && !empty) {
//
//            playerName.setText(item);
//            setGraphic(hbox);
//        }
//    }
}
