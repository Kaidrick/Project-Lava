package moe.ofs.backend.system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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

/**
 * @ProjectName: Project-Lava
 * @ClassName: Knife4jConfig
 * @Description:
 * @Author: alexpetertyler
 * @Date: 2020/12/28
 * @Version v1.0
 */
@Configuration
@EnableSwagger2WebMvc
@RequiredArgsConstructor
public class ApiDocConfig implements WebMvcConfigurer {
    private final Environment environment;
    //    判断当前环境是否为 '开发'，'测试'环境
//    private boolean b = StrUtil.containsAnyIgnoreCase(environment.getActiveProfiles()[0], "dev", "test");

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
//                .enable(b)
                .groupName("Debug")
                .select()
                .apis(RequestHandlerSelectors.basePackage("moe.ofs.backend.debug.controllers"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("北欧式的简单 ，Tyler997", "https://github.com/Kaidrick/Project-Lava", "");
        return new ApiInfoBuilder()
                .contact(contact)
                .title("Project Lava API Doc")
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
