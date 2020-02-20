package moe.ofs.backend.request.export;

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
