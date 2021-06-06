package moe.ofs.backend.system.config;

import cn.hutool.core.util.StrUtil;
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

@Configuration
@EnableSwagger2WebMvc
@RequiredArgsConstructor
public class ApiDocConfig implements WebMvcConfigurer {
    private final Environment environment;

    @Bean("debugDocket")
    public Docket debugDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
//                .enable(checkEnvironment())
                .groupName("Lua调试")
                .select()
                .apis(RequestHandlerSelectors.basePackage("moe.ofs.backend.debug.controllers"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean("addonDocket")
    public Docket addonDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
//                .enable(checkEnvironment())
                .groupName("插件")
                .select()
                .apis(RequestHandlerSelectors.basePackage("moe.ofs.backend.addons.controllers"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean("roleDocket")
    public Docket roleDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
//                .enable(checkEnvironment())
                .groupName("角色")
                .select()
                .apis(RequestHandlerSelectors.basePackage("moe.ofs.backend.function.admin.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean("AirdromeDocket")
    public Docket AirdromeDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
//                .enable(checkEnvironment())
                .groupName("机场")
                .select()
                .apis(RequestHandlerSelectors.basePackage("moe.ofs.backend.util"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean("configDocket")
    public Docket configDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
//                .enable(checkEnvironment())
                .groupName("脚本配置")
                .select()
                .apis(RequestHandlerSelectors.basePackage("moe.ofs.backend.config.controllers"))
                .paths(PathSelectors.ant("/config/**"))
                .build();
    }

    @Bean("serverDocket")
    public Docket serverDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
//                .enable(checkEnvironment())
                .groupName("服务器")
                .select()
                .apis(RequestHandlerSelectors.basePackage("moe.ofs.backend.config.controllers"))
                .paths(PathSelectors.ant("/server/**"))
                .build();
    }

//    @Bean
//    public Docket addonDocket() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
////                .enable(checkEnvironment())
//                .groupName("插件管理")
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("moe.ofs.backend.addons.controllers"))
//                .paths(PathSelectors.any())
//                .build();
//    }

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

    // 判断当前环境是否为 '开发'，'测试'环境
    private boolean checkEnvironment() {
        return StrUtil.containsAnyIgnoreCase(environment.getActiveProfiles()[0], "dev", "test");
    }
}
