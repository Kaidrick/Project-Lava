package moe.ofs.backend.dataservice.aspect;

import moe.ofs.backend.dataservice.graveyard.GraveyardService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraveyardCollectAspectConfiguration {

    private final GraveyardService graveyardService;

    public GraveyardCollectAspectConfiguration(GraveyardService graveyardService) {
        this.graveyardService = graveyardService;
    }

    @Bean
    public GraveyardCollectAspect graveyardCollectAspect() {
//        return Aspects.aspectOf(GraveyardCollectAspect.class);
        return new GraveyardCollectAspect(graveyardService);
    }
}
