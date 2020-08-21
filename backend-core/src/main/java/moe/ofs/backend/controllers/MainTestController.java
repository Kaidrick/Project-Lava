package moe.ofs.backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainTestController {

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String testConnection() {
        return "good";
    }
}
