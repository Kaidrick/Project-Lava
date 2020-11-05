package moe.ofs.backend.http.advice;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import moe.ofs.backend.http.GlobalDefaultProperties;
import moe.ofs.backend.http.Response;
import moe.ofs.backend.http.annotations.IgnoreResponseAdvice;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;

@RestControllerAdvice
public class ResponseDataAdvice implements ResponseBodyAdvice<Object> {
    private GlobalDefaultProperties globalDefaultProperties;

    public ResponseDataAdvice(GlobalDefaultProperties globalDefaultProperties) {
        this.globalDefaultProperties = globalDefaultProperties;
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

        // o is null -> return response
        if (o == null) {
            // 当 o 返回类型为 string 并且为null会出现 java.lang.ClassCastException: Result cannot be cast to java.lang.String
            if (methodParameter.getParameterType().getName().equals("java.lang.String")) {
                return new Gson().toJson(Response.success()).toString();
            }
            return Response.success();
        }

//        System.out.println("serverHttpRequest.getURI().getPath() = " + serverHttpRequest.getURI().getPath());

        if (serverHttpRequest.getURI().getPath().startsWith("/atlas")) {
            return o;
        }

        if (serverHttpRequest.getURI().getPath().startsWith("/actuator")) {
            return o;
        }

        if (serverHttpRequest.getURI().getPath().startsWith("/swagger")) {
            return o;
        }



        if (serverHttpRequest.getURI().getPath().equals("/error")) {
            // check source
            if (o instanceof LinkedHashMap) {
                LinkedHashMap<String, Object> response = (LinkedHashMap<String, Object>) o;
                Response<?> failResponse = Response.fail(null);
                failResponse.setMessage((String) response.get("message"));
                failResponse.setStatus((int) response.get("status"));
                System.out.println("response = " + response);
                return failResponse;
            }
        }

        // o is instanceof ConmmonResponse -> return o
        if (o instanceof Response) {
            return (Response<Object>) o;
        }
        // string 特殊处理 java.lang.ClassCastException: Result cannot be cast to java.lang.String
        if (o instanceof String) {
            return new Gson().toJson(Response.success(o)).toString();
        }

        return Response.success(o);
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
