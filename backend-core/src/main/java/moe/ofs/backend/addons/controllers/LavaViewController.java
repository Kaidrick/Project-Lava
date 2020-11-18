package moe.ofs.backend.addons.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LavaViewController {

//    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String index() {
        return "index.html";
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "/view/{ident}")
    public String test(@PathVariable String ident) {
        System.out.println("ident = " + ident);
        return "forward:/" + ident + "/index.html";
    }
}
