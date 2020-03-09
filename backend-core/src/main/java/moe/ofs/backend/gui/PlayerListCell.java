package moe.ofs.backend.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.Modality;
import moe.ofs.backend.ControlPanelApplication;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.object.FlyableUnit;
import moe.ofs.backend.repositories.PlayerInfoRepository;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.services.FlyableUnitService;
import moe.ofs.backend.util.LuaScripts;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerListCell extends ListCell<String> {

    private final static PlayerInfoRepository PLAYER_INFO_REPOSITORY =
            ControlPanelApplication.applicationContext.getBean(PlayerInfoRepository.class);

    private final static FlyableUnitService FLYABLE_UNIT_SERVICE =
            ControlPanelApplication.applicationContext.getBean(FlyableUnitService.class);

    ContextMenu contextMenu = new ContextMenu();

    EventHandler<ActionEvent> showPlayerDetail = event -> {
        // show a prompt dialog that contains the info for this player
        PlayerInfo playerInfo = PLAYER_INFO_REPOSITORY.findByName(getItem()).orElseThrow(
                () -> new RuntimeException("Unable to find PlayerInfo by player name: " + getItem())
        );
        long playerId = playerInfo.getNetId();
        String playerUcid = playerInfo.getUcid();
        String playerIpaddr = playerInfo.getIpaddr();
        String playerLang = playerInfo.getLang();
        int playerPing = playerInfo.getPing();
        String playerSlot = playerInfo.getSlot().equals("") ? "Observer" : "#" + playerInfo.getSlot();
        String unitName =
                playerInfo.getSlot().equals("") ?
                        "" :
                        "[" +
                        FLYABLE_UNIT_SERVICE.findByUnitId(playerInfo.getSlot()).orElse(new FlyableUnit()).getUnit_name() +
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
        alert.initOwner(ControlPanelApplication.stage);

        alert.show();
    };
    EventHandler<ActionEvent> kickPlayer = event -> {
        PlayerInfo playerInfo = PLAYER_INFO_REPOSITORY.findByName(getItem()).orElseThrow(
                () -> new RuntimeException("Unable to find PlayerInfo by player name: " + getItem())
        );

        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Kick Player");
        textInputDialog.setHeaderText(String.format("Confirm kicking player \"%s\"", playerInfo.getName()));
        textInputDialog.setContentText("Specify reason:");

        textInputDialog.initModality(Modality.APPLICATION_MODAL);
        textInputDialog.initOwner(ControlPanelApplication.stage);
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
        PlayerInfo playerInfo = PLAYER_INFO_REPOSITORY.findByName(getItem()).orElseThrow(
                () -> new RuntimeException("Unable to find PlayerInfo by player name: " + getItem())
        );

        BanPlayerOptionDialog dialog = new BanPlayerOptionDialog();

        dialog.setTitle("Ban Player");
        dialog.setHeaderText(String.format("Confirm banning player \"%s\"", playerInfo.getName()));
        dialog.setContentText("Specify reason:");

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(ControlPanelApplication.stage);
        Optional<BanPlayerOptionDialogResult> resultOptional = dialog.showAndWait();
        if(resultOptional.isPresent()) {
            BanPlayerOptionDialogResult result = resultOptional.get();
            String reason = !result.getReason().equals("") ? result.getReason() : "No reason specified.";
            LocalDate date = result.getDate();
            Duration duration;
            if(result.isPermanent()) {
                // permanent ban
                duration = Duration.ofDays(10000);
            } else {
                if(date != null) {
                    duration = Duration.between(LocalDateTime.now(),
                            LocalDateTime.of(result.getDate(), LocalTime.now()));
                } else {
                    duration = Duration.parse(String.format("P%dDT%dH%dM",
                            result.getDays(), result.getHours(), result.getMinutes()));
                }
            }
            Long banSeconds = duration.getSeconds();

            String banDescription = reason + "\\n" +
                    "Effective until " +
                    LocalDateTime.now().plus(duration) + "\\n" +
                    "Contact @NameSetByServer if you believe you are banned by mistake.";
            System.out.println("Ban " + playerInfo.getName() +
                    " for " + banSeconds + " seconds due to " + banDescription);
            String luaString = LuaScripts.loadAndPrepare("api/ban_net_player.lua",
                    playerInfo.getId(), banSeconds, banDescription);
                        new ServerExecRequest(RequestToServer.State.DEBUG, luaString).send();
        }
    };

    public PlayerListCell(ListView<String> listView) {

        this.textProperty().bind(this.itemProperty());

        this.itemProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue != null) {
                PlayerInfo playerInfo = PLAYER_INFO_REPOSITORY.findByName(getItem()).orElseThrow(
                        () -> new RuntimeException("Unable to find PlayerInfo by player name: " + getItem())
                );

                List<MenuItem> list = new ArrayList<>();

                MenuItem showDetailItem = new MenuItem();
                showDetailItem.setText("Detail");
                showDetailItem.setOnAction(showPlayerDetail);
                list.add(showDetailItem);

                if(playerInfo.getNetId() != 1) {  // if not server
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
