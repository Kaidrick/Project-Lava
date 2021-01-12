package moe.ofs.backend.function.motd.controller;

import moe.ofs.backend.function.motd.model.MotdMessageSet;
import moe.ofs.backend.function.motd.services.MotdManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("motd")
public class MotdManageController {

    private final MotdManageService service;

    public MotdManageController(MotdManageService service) {
        this.service = service;
    }

    @PostMapping("edit")
    public int edit(@RequestBody List<MotdMessageSet> messageList) {
        messageList.stream()
                .filter(m -> true)  // check message validity
                .forEach(service::save);

        return messageList.size();
    }

    @GetMapping("all")
    public Set<MotdMessageSet> get() {
        return service.findAll();
    }
}
