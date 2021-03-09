package moe.ofs.backend.security.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import moe.ofs.backend.dao.TokenInfoDao;
import moe.ofs.backend.domain.TokenInfo;
import moe.ofs.backend.security.annotation.CheckPermission;
import moe.ofs.backend.security.service.AccessTokenService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/authTest")
@RequiredArgsConstructor
public class SecurityTestController {
    private final TokenInfoDao tokenInfoDao;
    private final AccessTokenService accessTokenService;

    //    只有登录用户才能访问，不然跳转至登录页

    @GetMapping("/authorized")
    public String authorized() {
        return "您已登录";
    }

    @GetMapping("/everyone")
    public String everyone() {
        return "欢迎!";
    }

    //    只有“admin”组的用户才可访问
    @CheckPermission(groups = {"admin"}, requiredAccessToken = true)
    @GetMapping("/admin")
    public String admin() {
        return "欢迎您，admin!";
    }

    @GetMapping("/findAllToken")
    public Object findAll() {
        Map<String, Object> map = new HashMap<>();
        map.put("db", tokenInfoDao.selectList(null));
        map.put("map", accessTokenService.findAll());
        return map;
    }

    @GetMapping("/expireRefreshToken")
    public void expire() {
        TokenInfo tokenInfo = tokenInfoDao.selectOne(Wrappers.<TokenInfo>lambdaQuery().eq(TokenInfo::getUserId, 1));
        tokenInfo.setRefreshTokenExpireTime(DateUtil.yesterday());
        tokenInfoDao.updateById(tokenInfo);
    }
}
