package moe.ofs.backend.http;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AddonCustomViewInterceptorConfig implements WebMvcConfigurer {

    private final AddonCustomViewInterceptor interceptor;

    public AddonCustomViewInterceptorConfig(AddonCustomViewInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
    }
}
