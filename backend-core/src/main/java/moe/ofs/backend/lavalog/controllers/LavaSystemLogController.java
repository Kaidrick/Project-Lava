package moe.ofs.backend.lavalog.controllers;

import moe.ofs.backend.pagination.LavaSystemLogPageObject;
import moe.ofs.backend.pagination.PageVo;
import moe.ofs.backend.domain.LogEntry;
import moe.ofs.backend.lavalog.services.LavaSystemLogService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("syslog")
public class LavaSystemLogController {
    private final LavaSystemLogService service;

    public LavaSystemLogController(LavaSystemLogService service) {
        this.service = service;
    }

    @PostMapping("current")
    public PageVo<LogEntry> findByPage(@RequestBody LavaSystemLogPageObject object) {
        return service.findAllForCurrentSession(object.getCurrentPageNo(), object.getPageSize());
    }

    @PostMapping("page")
    public PageVo<LogEntry> findByCriteria(@RequestBody LavaSystemLogPageObject object) {
        return service.findLogsWithCriteria(object);
    }
}
