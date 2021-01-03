package moe.ofs.backend.discipline.controllers;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.discipline.model.PlayerDisciplinaryAction;
import moe.ofs.backend.discipline.model.Punishment;
import moe.ofs.backend.discipline.service.PlayerDisciplineService;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.services.PlayerDataService;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("discipline")
public class PlayerBanKickController {

    private final PlayerDisciplineService disciplineService;
    private final PlayerDataService playerInfoService;

    public PlayerBanKickController(PlayerDisciplineService disciplineService, PlayerDataService playerInfoService) {
        this.disciplineService = disciplineService;
        this.playerInfoService = playerInfoService;
    }

    @GetMapping("category")
    public List<Map<String, Object>> getPunishmentCategories() {
        return Arrays.stream(Punishment.values())
                .map(p -> {
                    Map<String, Object> map = new HashMap<>(2);
                    map.put("name", p.getTypeName());
                    map.put("type", p.getType());
                    return map;
                }).collect(Collectors.toList());
    }

    @RequestMapping(value = "punish", method = RequestMethod.POST)
    public boolean punishPlayer(@RequestBody PlayerDisciplinaryAction action) {
        log.info(action.toString());

        Optional<PlayerInfo> optional = playerInfoService.findByUcid(action.getUcid());

        // if optional is present, execute immediately
        if (optional.isPresent()) {
            PlayerInfo accused = optional.get();
            switch (action.getPunishment()) {
                case WARNING:
                    break;
                case DESTORY:  // find player unit by matching slot id and unit id
                    disciplineService.destroy(accused);
                    break;
                case EXPLOSION:
                    break;
                case KICK:
                    disciplineService.kick(accused, action.getReason());
                    break;
                case BAN:
                    disciplineService.ban(accused, action.getReason(), action.getDuration());
                    break;
                case PERMANENT_BAN:
                    disciplineService.ban(accused);
                    break;
                default:  // warning
                    break;
            }

            return true;
        }

        // TODO
        // else ucid is passed but player is not found in the service, defer to offline accusation



        return false;
    }

}
