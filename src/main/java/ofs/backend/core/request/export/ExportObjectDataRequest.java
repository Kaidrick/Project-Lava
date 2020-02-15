package ofs.backend.core.request.export;

import ofs.backend.core.request.RequestToExportAsync;

public class ExportObjectDataRequest extends RequestToExportAsync {
    {
        handle = Handle.QUERY;
        port = 3013;
    }


    @Override
    public void resolve(Object object) {

    }

    @Override
    public void resolve(String object) {

    }
}
