package moe.ofs.backend.http.security.provider;

import lombok.RequiredArgsConstructor;
import moe.ofs.backend.http.security.dao.AdminInfoDao;
import moe.ofs.backend.http.security.domain.AdminInfo;
import moe.ofs.backend.http.security.token.PasswordTypeToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PasswordTypeProvider implements AuthenticationProvider {
    private final AdminInfoDao adminInfoDao;

    @Override
    public Authentication authenticate(Authentication authentication) {

        String principal = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        AdminInfo adminInfo = adminInfoDao.selectById(principal);

        if (adminInfo == null) throw new RuntimeException("用户名不正确!");
        if (!password.equals(adminInfo.getPassword())) throw new RuntimeException("密码不正确!");

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("admin"));

        return new PasswordTypeToken(Collections.unmodifiableList(authorities), principal, null);

    }

    public boolean supports(Class<?> authentication) {
        return PasswordTypeToken.class.isAssignableFrom(authentication);
    }
}
