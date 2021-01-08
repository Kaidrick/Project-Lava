package moe.ofs.backend;

import moe.ofs.backend.aspects.LuaInteractPremiseAspect;
import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

// FIXME: why does it has to be placed in moe.ofs.backend package?
@Configuration
public class AspectConfiguration {

    @PostConstruct
    public void say() {
        System.out.println(getClass().getName());
    }

    @Bean
    public LuaInteractPremiseAspect luaInteractPremiseAspect() {
        return Aspects.aspectOf(LuaInteractPremiseAspect.class);
    }
}
