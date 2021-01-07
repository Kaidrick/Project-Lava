package moe.ofs.backend.aspects;

import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class AspectConfiguration {

    @Bean
    ExportObjectAspect exportObjectAspect() {
        return Aspects.aspectOf(ExportObjectAspect.class);
    }

    @Bean
    GraveyardCollectAspect graveyardCollectAspect() {
        return Aspects.aspectOf(GraveyardCollectAspect.class);
    }

    @Bean
    MethodOperationPhasePremiseAspect methodOperationPhasePremiseAspect() {
        return Aspects.aspectOf(MethodOperationPhasePremiseAspect.class);
    }

    @Bean
    PlayerInfoAspect playerInfoAspect() {
        return Aspects.aspectOf(PlayerInfoAspect.class);
    }

    @Bean
    PlayerInfoSlotChangeAspect playerInfoSlotChangeAspect() {
        return Aspects.aspectOf(PlayerInfoSlotChangeAspect.class);
    }
}
