package moe.ofs.backend.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Component
public class SuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
//        String name = userInfo.getName();
//        log.info("用户：" + name + " 登录成功\t时间：" + DateUtil.now());
//
//        DateTime date = DateUtil.date();
//        String today = DateUtil.year(date) + " 年 " + DateUtil.month(date) + " 月 " + DateUtil.dayOfMonth(date) + " 日";
        HttpSession session = request.getSession();
//        session.setAttribute("userName", name);
//        session.setAttribute("userId", userInfo.getId());
//        session.setAttribute("today", today);
//        log.info("******已存放用户信息的session ID：" + session.getId());

        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);

        String url=null;
//        if (savedRequest == null || savedRequest.getRedirectUrl().equals("http://" + ip + ":" + port + "/login")) {
//            url = "http://" + ip + ":" + webPort + "/";
//        } else {
//            url = savedRequest.getRedirectUrl();
//        }
        request.setAttribute("redirectUrl", url);
    }
}
