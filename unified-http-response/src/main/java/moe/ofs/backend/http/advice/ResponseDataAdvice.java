package moe.ofs.backend.http.advice;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import moe.ofs.backend.http.GlobalDefaultProperties;
import moe.ofs.backend.http.config.EndpointBypassProperties;
import moe.ofs.backend.http.response.Response;
import moe.ofs.backend.http.annotations.IgnoreResponseAdvice;
import moe.ofs.backend.http.response.Responses;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static moe.ofs.backend.http.response.Response.success;

@RestControllerAdvice
public class ResponseDataAdvice implements ResponseBodyAdvice<Object> {
    private GlobalDefaultProperties globalDefaultProperties;
    private final EndpointBypassProperties endpointBypassProperties;

    public ResponseDataAdvice(GlobalDefaultProperties globalDefaultProperties,
                              EndpointBypassProperties endpointBypassProperties) {
        this.globalDefaultProperties = globalDefaultProperties;
        this.endpointBypassProperties = endpointBypassProperties;
    }

    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> aClass) {
        return filter(methodParameter);
    }

    @SneakyThrows
    @Nullable
    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {

        // 返回值为 Object 类型  并且返回为空是  AbstractMessageConverterMethodProcessor#writeWithMessageConverters 方法
        // 无法触发调用本类的 beforeBodyWrite 处理，开发在 Controller 尽量避免直接使用 Object 类型返回。

        // advise bypasses any of the endpoints specified in the properties file
        if (endpointBypassProperties.getEndpoints().stream()
                .anyMatch(ep -> serverHttpRequest.getURI().getPath().startsWith(ep))) {
            return o;
        }

        // ignore advise if annotated by @IgnoreResponseAdvice
        if (!supports(methodParameter, aClass)) {
            return o;
        }

        if (o == null) {
            // 当 o 返回类型为 string 并且为null会出现 java.lang.ClassCastException: Result cannot be cast to java.lang.String
            if (methodParameter.getParameterType().getName().equals("java.lang.String")) {
                return new Gson().toJson(success());
            }
            return success();
        }

        // o is null -> return response
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) serverHttpResponse).getServletResponse();

        if (servletResponse.getStatus() != HttpServletResponse.SC_OK) {
            Response<?> failResponse = Response.fail();
            if (o instanceof Throwable) {
                failResponse.setMessage(((Throwable) o).getMessage());
            } else {
                failResponse.setMessage("Bad Request");
            }

            failResponse.setStatus((servletResponse.getStatus()));
            return failResponse;
        }

//        if (serverHttpRequest.getURI().getPath().equals("/error")) {
//            // check source
//            if (o instanceof LinkedHashMap) {
//                LinkedHashMap<String, Object> response = (LinkedHashMap<String, Object>) o;
//                Response<?> failResponse = Response.fail();
//                failResponse.setMessage((String) response.get("message"));
//                failResponse.setStatus((int) response.get("status"));
//                System.out.println("response = " + response);
//                return failResponse;
//            }
//        }

        if (o != null && methodParameter.getParameterType().getName().equals("moe.ofs.backend.domain.pagination.PageVo")) {
            return Responses.querySuccess(o);
        }

        // o is instanceof ConmmonResponse -> return o
        if (o instanceof Response) {
            return (Response<Object>) o;
        }
        // string 特殊处理 java.lang.ClassCastException: Result cannot be cast to java.lang.String
        if (o instanceof String) {
            return new Gson().toJson(success(o)).toString();
        }

        return success(o);
    }

    private Boolean filter(MethodParameter methodParameter) {
        Class<?> declaringClass = methodParameter.getDeclaringClass();
        // 检查过滤包路径
        long count = globalDefaultProperties.getAdviceFilterPackage().stream()
                .filter(l -> declaringClass.getName().contains(l)).count();
        if (count > 0) {
            return false;
        }
        // 检查<类>过滤列表
        if (globalDefaultProperties.getAdviceFilterClass().contains(declaringClass.getName())) {
            return false;
        }
        // 检查注解是否存在
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }
        if (methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }
        return true;
    }
}
