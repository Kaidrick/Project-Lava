package moe.ofs.backend.plugin.greeting;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.services.FlyableUnitService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class MessageQueueFactory implements FactoryBean<MessageQueue> {

    private ExportObject exportObject;

    private final FlyableUnitService flyableUnitService;

    public MessageQueueFactory(FlyableUnitService flyableUnitService) {
        this.flyableUnitService = flyableUnitService;
    }

    public ExportObject getExportObject() {
        return exportObject;
    }

    public void setExportObject(ExportObject exportObject) {
        this.exportObject = exportObject;
    }

    @Override
    public MessageQueue getObject() {
        return new MessageQueue(exportObject, flyableUnitService);
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
