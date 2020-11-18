package moe.ofs.backend.http;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AddonCustomViewInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().startsWith("/view")) {
            if (request.getHeader("Referer") != null) {
                System.out.println("request.getHeader(\"Referer\") = " + request.getHeader("Referer"));

                System.out.println("request = " + request.getRequestURI()
                        .replaceAll("view", request.getHeader("Referer")
                                .split("/")[request.getHeader("Referer").split("/").length - 1]));

                response.sendRedirect(request.getRequestURI()
                        .replaceAll("view", request.getHeader("Referer")
                                .split("/")[request.getHeader("Referer").split("/").length - 1]));

                return false;
            }
        }

        return true;
    }
}
