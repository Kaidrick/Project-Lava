package moe.ofs.backend.function.triggermessage;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.services.FlyableUnitService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class MessageQueueFactory implements FactoryBean<MessageQueue> {

    private ExportObject exportObject;

    private final FlyableUnitService flyableUnitService;

    private final TriggerMessageService triggerMessageService;

    public MessageQueueFactory(FlyableUnitService flyableUnitService,
                               TriggerMessageService triggerMessageService) {
        this.flyableUnitService = flyableUnitService;
        this.triggerMessageService = triggerMessageService;
    }

    public ExportObject getExportObject() {
        return exportObject;
    }

    public void setExportObject(ExportObject exportObject) {
        this.exportObject = exportObject;
    }

    @Override
    public MessageQueue getObject() {
        return new MessageQueue(exportObject, flyableUnitService, triggerMessageService);
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
