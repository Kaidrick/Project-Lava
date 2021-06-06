package moe.ofs.backend.security.provider;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import moe.ofs.backend.dao.AdminInfoDao;
import moe.ofs.backend.domain.AdminInfo;
import moe.ofs.backend.security.exception.authentication.BadLoginCredentialsException;
import moe.ofs.backend.security.exception.token.InvalidAccessTokenException;
import moe.ofs.backend.security.service.AdminInfoService;
import moe.ofs.backend.security.token.PasswordTypeToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PasswordTypeProvider implements AuthenticationProvider {
    private final AdminInfoDao adminInfoDao;
    private final AdminInfoService adminInfoService;

    @Override
    public Authentication authenticate(Authentication authentication) {

        String principal = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        AdminInfo adminInfo = adminInfoDao.selectOne(Wrappers.<AdminInfo>lambdaQuery().eq(AdminInfo::getName, principal).eq(AdminInfo::getPassword, password));

        if (adminInfo == null) throw new BadLoginCredentialsException("用户名或密码不正确");
        addAdminInfoDto(adminInfo);
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (String role : adminInfo.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return new PasswordTypeToken(authorities, adminInfo, null);
    }

    public Authentication authenticate(String accessToken) {
        AdminInfo adminInfo = adminInfoDao.selectOneByAccessToken(accessToken);
        if (adminInfo == null) throw new InvalidAccessTokenException("AccessToken已过期或非法");

        addAdminInfoDto(adminInfo);

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : adminInfo.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return new PasswordTypeToken(authorities, adminInfo, null);
    }

    private void addAdminInfoDto(AdminInfo adminInfo) {
        adminInfo.setPassword(null);
        adminInfoService.getOneByName(adminInfo.getName());
    }

    public boolean supports(Class<?> authentication) {
        return PasswordTypeToken.class.isAssignableFrom(authentication);
    }
}
