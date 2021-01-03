package moe.ofs.backend.discipline.controllers;

import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.services.PlayerDataService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("player")
public class PlayerInfoListController {
    private final PlayerDataService service;

    public PlayerInfoListController(PlayerDataService service) {
        this.service = service;
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public Set<PlayerInfo> getPlayerList() {
        return service.findAll();
    }
}
