package moe.ofs.backend.http;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AddonCustomViewInterceptor implements HandlerInterceptor {
    public static final String REFERER = "Referer";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler)
            throws Exception {
        if (request.getRequestURI().startsWith("/view")) {
//            if (request.getHeader("Sec-Fetch-Dest").equals("iframe")) {
//                System.out.println("request = " + request);
//            } else {  // direct view
//                if (request.getHeader("Referer") != null) {
//
//                    String path = request.getRequestURI()
//                            .replaceAll("view", request.getHeader("Referer")
//                                    .split("/")[request.getHeader("Referer").split("/").length - 1]);
//                    RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(path);
//
//                    System.out.println("forward to path: " + path);
//
//                    dispatcher.forward(request, response);
//
//                    return false;
//                }
//            }

            // only redirect resource fetching request
            if (request.getHeader(REFERER) != null &&
                    !request.getHeader("Sec-Fetch-Dest").equals("iframe")) {
//                System.out.println("request.getHeader(\"Referer\") = " + request.getHeader("Referer"));
//                System.out.println("request.getRequestURI() = " + request.getRequestURI());

                String path = request.getHeader(REFERER)
                        .split("/")[request.getHeader(REFERER).split("/").length - 1];

//                System.out.println("path = " + path);

                String redirect = request.getRequestURI()
                        .replaceAll("^.?(?<!view)(/view)", "/" + path);

//                System.out.println("redirect = " + redirect);

                response.sendRedirect(redirect);

                return false;
            }
        }

        return true;
    }
}
