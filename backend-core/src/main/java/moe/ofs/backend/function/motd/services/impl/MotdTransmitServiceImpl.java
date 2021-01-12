package moe.ofs.backend.function.motd.services.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import moe.ofs.backend.annotations.ListenLavaMessage;
import moe.ofs.backend.dataservice.player.PlayerInfoService;
import moe.ofs.backend.domain.dcs.poll.ExportObject;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.function.admin.services.NetPlayerRoleService;
import moe.ofs.backend.function.motd.services.MotdManageService;
import moe.ofs.backend.function.motd.services.MotdTransmitService;
import moe.ofs.backend.function.triggermessage.factories.MessageQueueFactory;
import moe.ofs.backend.function.triggermessage.model.MessageQueue;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.Optional;

@Service
public class MotdTransmitServiceImpl implements MotdTransmitService {
    private final MessageQueueFactory factory;

    private final NetPlayerRoleService netPlayerRoleService;
    private final PlayerInfoService playerInfoService;

    private final MotdManageService service;

    public MotdTransmitServiceImpl(MessageQueueFactory factory, NetPlayerRoleService netPlayerRoleService, PlayerInfoService playerInfoService, MotdManageService service) {
        this.factory = factory;
        this.netPlayerRoleService = netPlayerRoleService;
        this.playerInfoService = playerInfoService;
        this.service = service;
    }

    @Async
    @Override
    public void transmit(ExportObject exportObject) {

        factory.setExportObject(exportObject);
        MessageQueue queue = factory.getObject();

        if (queue != null) {
            // find occupant player
            Optional<PlayerInfo> optional = playerInfoService.findByName(exportObject.getUnitName());

            optional.ifPresent(playerInfo -> {
                boolean authorized = netPlayerRoleService.findPlayerRoles(playerInfo).stream()
                        .anyMatch(playerRole -> playerRole.getRoleLevel() == 1001L);
                if (authorized) {
                    service.findAll().stream()
                            .peek(System.out::println)
                            .forEach(motdMessageSet ->
                            motdMessageSet.getMessages().forEach(queue::pend));

                    queue.send();
                }
            });
        }
    }

    @ListenLavaMessage(destination = "lava.spawn-control.export-object", selector = "type = 'spawn'")
    @Override
    public void trigger(Object trigger) {
        if (trigger instanceof TextMessage) {
            try {
                ExportObject exportObject =
                        new Gson().fromJson(((TextMessage) trigger).getText(), ExportObject.class);
                if (exportObject.getStatus().get("Human")) {
                    transmit(exportObject);
                }
            } catch (JsonSyntaxException | JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
