package moe.ofs.backend.lavalog.controllers;

import moe.ofs.backend.config.model.PageVo;
import moe.ofs.backend.domain.LogEntry;
import moe.ofs.backend.lavalog.LavaSystemLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("syslog")
public class LavaSystemLogController {
    private final LavaSystemLogService service;

    public LavaSystemLogController(LavaSystemLogService service) {
        this.service = service;
    }

    @GetMapping("page")
    public PageVo<LogEntry> findByPage() {
        return service.findAllForCurrentSession(null, 1L, 10);
    }
}
