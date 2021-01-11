package moe.ofs.backend;

import moe.ofs.backend.aspects.*;
import moe.ofs.backend.aspects.hookinterceptor.HookInterceptorAspect;
import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// FIXME: why does it has to be placed in moe.ofs.backend package?
@Configuration
public class AspectConfiguration {

    @Bean
    public LuaInteractPremiseAspect luaInteractPremiseAspect() {
        return Aspects.aspectOf(LuaInteractPremiseAspect.class);
    }

    @Bean
    public HookInterceptorAspect hookInterceptorAspect() {
        return Aspects.aspectOf(HookInterceptorAspect.class);
    }

    @Bean
    public ExportObjectAspect exportObjectAspect() {
        return Aspects.aspectOf(ExportObjectAspect.class);
    }

    @Bean
    public StaticObjectAspect staticObjectAspect() {
        return Aspects.aspectOf(StaticObjectAspect.class);
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
