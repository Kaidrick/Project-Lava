package moe.ofs.backend.dataservice.aspect;

import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraveyardCollectAspectConfiguration {

    @Bean
    public GraveyardCollectAspect graveyardCollectAspect() {
        return Aspects.aspectOf(GraveyardCollectAspect.class);
    }
}
