package moe.ofs.backend.lavalog.controllers;

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

    @PostMapping("page")
    public PageVo<LogEntry> findByPage(@RequestBody Map<String, String> map) {
        System.out.println("map = " + map);
        return service.findAllForCurrentSession(new Date(Long.parseLong(map.get("date"))), Long.parseLong(map.get("current")), Integer.parseInt(map.get("size")));
    }
}
