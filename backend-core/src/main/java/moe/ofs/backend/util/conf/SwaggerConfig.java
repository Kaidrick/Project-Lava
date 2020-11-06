package moe.ofs.backend.util.conf;

import cn.hutool.core.util.StrUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig implements WebMvcConfigurer {
//    private boolean b;

    //判断是否在dev环境
//    public SwaggerConfig(Environment environment) {
//        b = StrUtil.containsAnyIgnoreCase(environment.getActiveProfiles()[0], "dev");
//    }

    @Bean
    public Docket docket1() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("配置")
//                .enable(b)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("moe.ofs.backend.config.controllers.DcsConnectionConfigController"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("Kaidrick", "https://github.com/Kaidrick", "");
        return new ApiInfoBuilder()
                .contact(contact)
                .title("Project-Lava")
                .description("A framework for DCS World and a solution to mission scripting, server management and data analysis, packed with a GUI control panel.")
                .version("v1.0")
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
