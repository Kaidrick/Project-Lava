package core.request.export;

import core.request.RequestToExportAsync;

public class ExportFillerRequest extends RequestToExportAsync {

    {
        handle = Handle.EMPTY;
        port = 3013;
    }

    @Override
    public void resolve(String object) {

    }

    @Override
    public void resolve(Object object) {

    }
}
