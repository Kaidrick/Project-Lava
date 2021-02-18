package moe.ofs.backend.function.motd.controller;

import moe.ofs.backend.dao.MotdDao;
import moe.ofs.backend.domain.admin.message.MotdMessageSet;
import moe.ofs.backend.function.motd.services.MotdManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("motd")
public class MotdManageController {

    private final MotdManageService service;

    private final MotdDao motdDao;

    public MotdManageController(MotdManageService service, MotdDao motdDao) {
        this.service = service;
        this.motdDao = motdDao;
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

    @GetMapping("test-all")
    public Set<MotdMessageSet> find() {
        return motdDao.findAllMotdMessageSet();
    }
}
