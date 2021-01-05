package moe.ofs.backend.http.security.filter;

import moe.ofs.backend.http.security.token.PasswordTypeToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class PasswordTypeFilter extends AbstractAuthenticationProcessingFilter {

    public PasswordTypeFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("必须使用POST请求！");
        }

        String principal = Optional.ofNullable(request.getParameter("principal"))
                .orElseThrow(() -> new RuntimeException("用户名不能为空"));

        String password = Optional.ofNullable(request.getParameter("password"))
                .orElseThrow(() -> new RuntimeException("密码不能为空"));

        PasswordTypeToken authRequest = new PasswordTypeToken(principal, password);
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        return this.getAuthenticationManager().authenticate(authRequest);
    }

}

