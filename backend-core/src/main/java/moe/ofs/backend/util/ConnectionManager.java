package moe.ofs.backend.util;

import moe.ofs.backend.request.BaseRequest;
import moe.ofs.backend.request.RequestHandler;
import moe.ofs.backend.request.server.ServerFillerRequest;

public class ConnectionManager {

    public static void sanitizeDataPipeline(RequestHandler<BaseRequest> requestHandler) {
        new ServerFillerRequest() {
            { handle = Handle.EMPTY; port = 3010; }
        }.send();
        new ServerFillerRequest() {
            { handle = Handle.EMPTY; port = 3012; }
        }.send();

        try {
            requestHandler.transmitAndReceive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
