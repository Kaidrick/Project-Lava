package moe.ofs.backend.http;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.regex.Pattern;

@Component
public class AddonCustomViewInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().startsWith("/view")) {
            if (request.getHeader("Sec-Fetch-Dest").equals("iframe")) {
                System.out.println("request = " + request);
            } else {  // direct view
                if (request.getHeader("Referer") != null) {

                    String path = request.getRequestURI()
                            .replaceAll("view", request.getHeader("Referer")
                                    .split("/")[request.getHeader("Referer").split("/").length - 1]);
                    RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(path);

                    dispatcher.forward(request, response);
//
//                    response.sendRedirect(request.getRequestURI()
//                            .replaceAll("view", request.getHeader("Referer")
//                                    .split("/")[request.getHeader("Referer").split("/").length - 1]));

                    return false;
                }
            }
        }

        return true;
    }
}
