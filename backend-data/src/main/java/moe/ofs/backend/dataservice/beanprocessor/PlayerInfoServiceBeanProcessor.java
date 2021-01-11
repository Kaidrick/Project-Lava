package moe.ofs.backend.dataservice.beanprocessor;

import moe.ofs.backend.dataservice.player.PlayerInfoService;
import moe.ofs.backend.dataservice.aware.PlayerInfoServiceAware;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PlayerInfoServiceBeanProcessor implements BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName)
            throws BeansException {
        if (bean instanceof PlayerInfoServiceAware) {
            ((PlayerInfoServiceAware) bean).setPlayerInfoService(applicationContext.getBean(PlayerInfoService.class));
        }

        return bean;
    }
}
