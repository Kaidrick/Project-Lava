package moe.ofs.backend.http.security.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ProjectName: mybox
 * @ClassName: SuccessHandler
 * @Description:
 * @Author: alexpetertyler
 * @Date: 2020/9/16
 * @Version v1.0
 */
@Component
public class FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse
            response, AuthenticationException exception) throws IOException {

//        强制用户跳转
//        new DefaultRedirectStrategy().sendRedirect(request, response, "http://" + ip + ":" + port + "/");
    }
}
