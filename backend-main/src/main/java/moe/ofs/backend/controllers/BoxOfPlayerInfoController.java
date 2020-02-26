package moe.ofs.backend.controllers;

import moe.ofs.backend.box.BoxOfPlayerInfo;
import moe.ofs.backend.object.PlayerInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BoxOfPlayerInfoController {

    @RequestMapping("player")
    public String getAllPlayers(Model model) {

        model.addAttribute("players", BoxOfPlayerInfo.peek().values());

        return "player/index";
    }

}
