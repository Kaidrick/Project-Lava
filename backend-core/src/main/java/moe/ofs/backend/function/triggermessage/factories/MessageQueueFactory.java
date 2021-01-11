package moe.ofs.backend.function.triggermessage.factories;

import moe.ofs.backend.domain.dcs.poll.ExportObject;
import moe.ofs.backend.function.triggermessage.model.MessageQueue;
import moe.ofs.backend.function.triggermessage.services.TriggerMessageService;
import moe.ofs.backend.dataservice.slotunit.FlyableUnitService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class MessageQueueFactory implements FactoryBean<MessageQueue>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final ThreadLocal<ExportObject> objectThreadLocal;

    public MessageQueueFactory() {
        objectThreadLocal = new ThreadLocal<>();
    }

    public ExportObject getExportObject() {
        return objectThreadLocal.get();
    }

    public void setExportObject(ExportObject exportObject) {
        this.objectThreadLocal.set(exportObject);
    }

    @Override
    public MessageQueue getObject() {
        try {
            return new MessageQueue(objectThreadLocal.get(),
                    applicationContext.getBean(FlyableUnitService.class),
                    applicationContext.getBean(TriggerMessageService.class));
        } finally {
            objectThreadLocal.remove();
        }
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
