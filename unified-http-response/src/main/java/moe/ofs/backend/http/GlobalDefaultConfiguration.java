package moe.ofs.backend.http;

import moe.ofs.backend.http.advice.ResponseDataAdvice;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(GlobalDefaultProperties.class)
@PropertySource(value = "classpath:http-response.properties", encoding = "UTF-8")
public class GlobalDefaultConfiguration {

//    @Bean
//    public GlobalDefaultExceptionHandler globalDefaultExceptionHandler() {
//        return new GlobalDefaultExceptionHandler();
//    }

//    @Bean
//    public ResponseDataAdvice commonResponseDataAdvice(GlobalDefaultProperties globalDefaultProperties) {
//        return new ResponseDataAdvice(globalDefaultProperties);
//    }

}