package moe.ofs.backend.http.security.provider;

import moe.ofs.backend.http.security.domain.BasicUserInfo;
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
public class PasswordTypeProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) {

        String principal = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        if (!principal.equals("XXX")) throw new RuntimeException("用户名不正确!");
        if (!password.equals("XXX")) throw new RuntimeException("密码不正确!");

        List<GrantedAuthority> authorities = new ArrayList<>();
        BasicUserInfo basicUserInfo = new BasicUserInfo();
        basicUserInfo.getRoles().forEach(v -> authorities.add(new SimpleGrantedAuthority(v)));

        return new PasswordTypeToken(Collections.unmodifiableList(authorities), principal, null);
    }

    public boolean supports(Class<?> authentication) {
        return PasswordTypeToken.class.isAssignableFrom(authentication);
    }
}
