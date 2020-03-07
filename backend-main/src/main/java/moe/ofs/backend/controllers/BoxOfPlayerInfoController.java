package moe.ofs.backend.controllers;

import moe.ofs.backend.repositories.PlayerInfoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BoxOfPlayerInfoController {

    private final PlayerInfoRepository playerInfoRepository;

    public BoxOfPlayerInfoController(PlayerInfoRepository playerInfoRepository) {
        this.playerInfoRepository = playerInfoRepository;
    }

    @RequestMapping("player")
    public String getAllPlayers(Model model) {

        model.addAttribute("players", playerInfoRepository.findAll());

        return "player/index";
    }

}
