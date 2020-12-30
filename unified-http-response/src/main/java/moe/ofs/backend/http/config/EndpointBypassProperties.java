package moe.ofs.backend.http.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@PropertySource(value = "classpath:advise-bypass.properties", encoding = "UTF-8")
@ConfigurationProperties(prefix = "moe.ofs.http.advise-bypass")
@Getter
@Setter
public class EndpointBypassProperties {
    List<String> endpoints;
}
