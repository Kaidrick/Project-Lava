package moe.ofs.backend.connector.request.export;

import moe.ofs.backend.LavaLog;
import moe.ofs.backend.connector.request.Handle;
import moe.ofs.backend.domain.connector.Level;

import moe.ofs.backend.connector.request.BaseRequest;
import moe.ofs.backend.connector.response.Resolvable;

public class ExportExecRequest extends BaseRequest implements Resolvable {

    private final LavaLog.Logger logger = LavaLog.getLogger(ExportExecRequest.class);

    private transient String luaString;

    public ExportExecRequest(String luaString) {
        super(Level.EXPORT);
        handle = Handle.EXEC;

        this.luaString = luaString;
    }

    @Override
    public void resolve(String object) {
        String logMessage = luaString + "\nReturns: " + object;
        logger.debug(logMessage);
    }
}
