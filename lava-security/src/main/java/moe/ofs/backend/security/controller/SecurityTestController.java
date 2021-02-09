package moe.ofs.backend.security.controller;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authTest")
public class SecurityTestController {

    //    只有登录用户才能访问，不然跳转至登录页
//    @PreAuthorize("isAuthenticated()")
    @PostAuthorize("isAuthenticated()")
    @GetMapping("/authorized")
    public String authorized() {
        return "您已登录";
    }

    @GetMapping("/everyone")
    public String everyone() {
        return "欢迎!";
    }

    //    只有拥有“admin”权限的用户才可访问
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/admin")
    public String admin() {
        return "欢迎您，admin!";
    }
}
