package moe.ofs.backend.services;

import moe.ofs.backend.domain.ExportObject;

public interface ExportObjectService extends UpdatableService<ExportObject>, CrudService<ExportObject> {

    void dispose();

}
