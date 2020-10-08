package moe.ofs.backend.util.controllers;

import moe.ofs.backend.util.TheaterMapLandDataCollector;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TheaterMapLandDataCollectController {

    @RequestMapping("/data/land")
    public void getData() {
        try {
            TheaterMapLandDataCollector.fetchData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
