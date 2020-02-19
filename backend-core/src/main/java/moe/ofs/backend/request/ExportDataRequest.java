package moe.ofs.backend.request;

import moe.ofs.backend.request.export.RequestToExportAsync;

public class ExportDataRequest extends RequestToExportAsync {
    {
        handle = Handle.QUERY;
    }

    @Override
    public void resolve(String object) {
        System.out.println(object.toString());
    }

    @Override
    public void resolve(Object object) {

    }
}
