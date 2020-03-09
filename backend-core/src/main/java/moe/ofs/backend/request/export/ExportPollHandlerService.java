package moe.ofs.backend.request.export;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.request.AbstractPollHandlerService;
import moe.ofs.backend.services.UpdatableService;
import moe.ofs.backend.util.GenericClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class ExportPollHandlerService extends AbstractPollHandlerService<ExportObject> {

    @Autowired
    public ExportPollHandlerService(UpdatableService<ExportObject> exportObjectService) {
        super(exportObjectService);

        setGeneric(new GenericClass<>(ExportObject.class));
        setFlipThreshold(20);
    }
}
