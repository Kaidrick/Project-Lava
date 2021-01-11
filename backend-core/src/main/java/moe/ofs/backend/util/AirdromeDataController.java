package moe.ofs.backend.util;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("airdata")
public class AirdromeDataController {

    @GetMapping("collect")
    public String collectData() {
        new Thread(AirdromeDataCollector::collect).start();
        return "OK";
    }
}
