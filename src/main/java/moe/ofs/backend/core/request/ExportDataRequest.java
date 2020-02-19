package moe.ofs.backend.core.request;

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
