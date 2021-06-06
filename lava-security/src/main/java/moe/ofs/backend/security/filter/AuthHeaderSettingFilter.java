package moe.ofs.backend.security.filter;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import moe.ofs.backend.domain.LavaUserToken;
import moe.ofs.backend.dto.AdminInfoDto;
import moe.ofs.backend.dto.BaseUserInfoDto;
import moe.ofs.backend.security.provider.PasswordTypeProvider;
import moe.ofs.backend.security.service.AccessTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthHeaderSettingFilter implements Filter {
    private final AccessTokenService accessTokenService;
    private final PasswordTypeProvider passwordTypeProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String accessToken = request.getHeader("access_token");

        request.setAttribute("accessTokenValidate", false);

        if (!StrUtil.isBlank(accessToken) && accessTokenService.checkAccessToken(accessToken)) {
            request.setAttribute("accessTokenValidate", true);
            LavaUserToken lavaUserToken = accessTokenService.getByAccessToken(accessToken);
            BaseUserInfoDto baseUserInfoDto = lavaUserToken.getBaseUserInfoDto();
            if (baseUserInfoDto.getClassName().equals(AdminInfoDto.class.getSimpleName())) {
                Authentication authenticate = passwordTypeProvider.authenticate(accessToken);
                setAuthentication(authenticate);
            }
        }

        filterChain.doFilter(request, servletResponse);
    }

    void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
