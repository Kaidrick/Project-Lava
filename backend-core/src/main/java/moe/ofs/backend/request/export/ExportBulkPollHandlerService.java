package moe.ofs.backend.request.export;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.request.AbstractBulkPollHandlerService;
import moe.ofs.backend.services.UpdatableService;
import moe.ofs.backend.util.GenericClass;
import org.springframework.stereotype.Service;

@Service("exportObjectBulk")
public class ExportBulkPollHandlerService extends AbstractBulkPollHandlerService<ExportObject> {

    public ExportBulkPollHandlerService(UpdatableService<ExportObject> service) {
        super(service);

        setGeneric(new GenericClass<>(ExportObject.class));

        setFlipThreshold(10);
    }
}
