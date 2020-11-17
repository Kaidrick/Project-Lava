package moe.ofs.backend.connector;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LavaViewController {

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String index() {
        return "index.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/bad")
    public String test() {
        return "test.html";
    }
}
