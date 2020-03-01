package moe.ofs.backend.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.Modality;
import moe.ofs.backend.BackendMain;
import moe.ofs.backend.box.BoxOfFlyableUnit;
import moe.ofs.backend.box.BoxOfPlayerInfo;
import moe.ofs.backend.object.FlyableUnit;
import moe.ofs.backend.object.PlayerInfo;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.util.LuaScripts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerListCell extends ListCell<String> {

    EventHandler<ActionEvent> showPlayerDetail = event -> {
        // show a prompt dialog that contains the info for this player
        PlayerInfo playerInfo = BoxOfPlayerInfo.findByName(getItem());
        int playerId = playerInfo.getId();
        String playerUcid = playerInfo.getUcid();
        String playerIpaddr = playerInfo.getIpaddr();
        String playerLang = playerInfo.getLang();
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
                        "Client Language: %s" + "\n" +
                        "Ping: %sms" + "\n" +
                        "Slot: %s %s",
                playerId, playerUcid, playerIpaddr, playerLang, playerPing,
                playerSlot, unitName));

        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(BackendMain.stage);

        alert.show();
    };
    EventHandler<ActionEvent> kickPlayer = event -> {
        PlayerInfo playerInfo = BoxOfPlayerInfo.findByName(getItem());

        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Kick Player");
        textInputDialog.setHeaderText(String.format("Confirm kicking player \"%s\"", playerInfo.getName()));
        textInputDialog.setContentText("Specify reason:");

        textInputDialog.initModality(Modality.APPLICATION_MODAL);
        textInputDialog.initOwner(BackendMain.stage);
        Optional<String> kickReasonOptional = textInputDialog.showAndWait();
        if(kickReasonOptional.isPresent()) {
            String reason = kickReasonOptional.get();
            System.out.println("kick " + playerInfo.getName() + " -> " + reason);
            String luaString = LuaScripts.loadAndPrepare("api/kick_net_player.lua",
                    playerInfo.getId(), !reason.equals("") ? reason : "Server specifies no kick reason.");
            new ServerExecRequest(RequestToServer.State.DEBUG, luaString).send();
        }
    };
    EventHandler<ActionEvent> banPlayer = event -> {
        PlayerInfo playerInfo = BoxOfPlayerInfo.findByName(getItem());
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Ban Player");
        textInputDialog.setHeaderText(String.format("Confirm banning player \"%s\"", playerInfo.getName()));
        textInputDialog.setContentText("Specify reason:");

        textInputDialog.initModality(Modality.APPLICATION_MODAL);
        textInputDialog.initOwner(BackendMain.stage);

        // TODO --> do custom dialog, get reason and ban duration fields of day, hours, min
        Optional<String> kickReasonOptional = textInputDialog.showAndWait();
        if(kickReasonOptional.isPresent()) {
            String reason = kickReasonOptional.get();
            System.out.println("ban " + playerInfo.getName() + " -> " + reason);
            String luaString = LuaScripts.loadAndPrepare("api/ban_net_player.lua",
                    playerInfo.getId(), Long.MAX_VALUE, !reason.equals("") ? reason : "Server specifies no ban reason.");
                        new ServerExecRequest(RequestToServer.State.DEBUG, luaString).send();
        }
    };

    ContextMenu contextMenu = new ContextMenu();

    public PlayerListCell(ListView<String> listView) {

        this.textProperty().bind(this.itemProperty());

        this.itemProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue != null) {
                PlayerInfo playerInfo = BoxOfPlayerInfo.findByName(newValue);
//                System.out.println(playerInfo);

                List<MenuItem> list = new ArrayList<>();

                MenuItem showDetailItem = new MenuItem();
                showDetailItem.setText("Detail");
                showDetailItem.setOnAction(showPlayerDetail);
                list.add(showDetailItem);

                if(playerInfo.getId() != 1) {  // if not server
                    MenuItem kickPlayerItem = new MenuItem();
                    kickPlayerItem.setText("Kick");
                    kickPlayerItem.setOnAction(kickPlayer);

                    MenuItem banPlayerItem = new MenuItem();
                    banPlayerItem.setText("Ban");
                    banPlayerItem.setOnAction(banPlayer);

                    list.add(kickPlayerItem);
                    list.add(banPlayerItem);
                }

                this.contextMenu.getItems().setAll(list);
                this.setContextMenu(contextMenu);
            }

        }));

        this.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
            if (isNowEmpty) {
                this.setContextMenu(null);
            } else {

            }
        });
    }
}
