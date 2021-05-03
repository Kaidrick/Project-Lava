package moe.ofs.backend.security.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import moe.ofs.backend.dao.AdminInfoDao;
import moe.ofs.backend.domain.AdminInfo;
import moe.ofs.backend.domain.LavaUserToken;
import moe.ofs.backend.dto.AdminInfoDto;
import moe.ofs.backend.security.annotation.CheckPermission;
import moe.ofs.backend.security.provider.PasswordTypeProvider;
import moe.ofs.backend.security.service.AccessTokenService;
import moe.ofs.backend.security.service.AdminInfoService;
import moe.ofs.backend.security.token.PasswordTypeToken;
import moe.ofs.backend.vo.AdminInfoVo;
import org.springframework.security.core.Authentication;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final AdminInfoDao adminInfoDao;
    private final AccessTokenService accessTokenService;
    private final AdminInfoService adminInfoService;
    private final PasswordTypeProvider passwordTypeProvider;

    @PostMapping("/validate")
    public AdminInfo validateRegisteredUser(
            String username,
            String password
    ) {
        PasswordTypeToken token = new PasswordTypeToken(username, password);
        Authentication authenticate = passwordTypeProvider.authenticate(token);
        AdminInfo adminInfo = (AdminInfo) authenticate.getPrincipal();
        AdminInfo confirm = new AdminInfo();
        confirm.setEnable(adminInfo.getEnable());
        confirm.setName(adminInfo.getName());

        return confirm;
    }

    @PostMapping("/user_info/update")
    @CheckPermission(requiredAccessToken = true)
    public AdminInfoVo updateUserInfo(
            @RequestBody AdminInfoVo adminInfoVo,
            Authentication authentication
    ) {
        AdminInfo adminInfo = (AdminInfo) authentication.getPrincipal();
        String name = adminInfo.getName();
        adminInfo.setName(adminInfoVo.getName());

        adminInfoDao.updateById(adminInfo);
        AdminInfoDto adminInfoDto = adminInfoService.getOneById(adminInfo.getId());
        adminInfoDto.setName(name);
        adminInfoService.add(adminInfoDto);

        LavaUserToken lavaUserToken = accessTokenService.getByUserName(name);
        lavaUserToken.setBaseUserInfoDto(adminInfoDto);
        accessTokenService.add(lavaUserToken);

        return new AdminInfoVo(adminInfo.getId(), name);
    }


    @PostMapping("/user_info/get")
    @CheckPermission(requiredAccessToken = true)
    public AdminInfoVo getUserInfo(
            Authentication authentication
    ) {
        AdminInfo adminInfo = (AdminInfo) authentication.getPrincipal();
        return new AdminInfoVo(adminInfo.getId(), adminInfo.getName());
    }

    @PostMapping("/password/update")
    @CheckPermission(requiredAccessToken = true)
    public String changeUserPassword(
            @RequestParam("old_password") String oldOne,
            @RequestParam("new_password") String newOne,
            Authentication authentication
    ) {
        String name = authentication.getName();
        oldOne = DigestUtils.md5DigestAsHex(oldOne.getBytes());
        newOne = DigestUtils.md5DigestAsHex(newOne.getBytes());
        AdminInfo adminInfo = adminInfoDao.selectOne(Wrappers.<AdminInfo>lambdaQuery().eq(AdminInfo::getName, name).eq(AdminInfo::getPassword, oldOne));
        if (adminInfo == null) return "旧密码输入错误！";
        adminInfo.setPassword(newOne);
        adminInfoDao.updateById(adminInfo);
        return "密码更新成功！";
    }
}
