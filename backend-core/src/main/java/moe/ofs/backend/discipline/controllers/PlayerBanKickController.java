package moe.ofs.backend.discipline.controllers;

import moe.ofs.backend.discipline.model.PlayerDisciplinaryAction;
import moe.ofs.backend.discipline.service.PlayerDisciplineService;
import moe.ofs.backend.domain.PlayerInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("discipline")
public class PlayerBanKickController {

    private final PlayerDisciplineService service;

    public PlayerBanKickController(PlayerDisciplineService service) {
        this.service = service;
    }

    @RequestMapping(value = "punish", method = RequestMethod.POST)
    public boolean punishPlayer(PlayerDisciplinaryAction action) {
        PlayerInfo playerInfo = action.getPlayerInfo();

        switch (action.getPunishment()) {
            case WARNING:
                break;
            case DESTORY:
                break;
            case EXPLOSION:
                break;
            case KICK:
                service.kick(playerInfo, action.getReason());
                break;
            case BAN:
                service.ban(playerInfo, action.getReason(), action.getDuration());
                break;
            case PERMANENT_BAN:
                service.ban(playerInfo);
                break;
            default:  // warning
                break;
        }
        return true;
    }

}
