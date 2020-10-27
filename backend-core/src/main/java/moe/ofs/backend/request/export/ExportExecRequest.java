package moe.ofs.backend.request.export;

import moe.ofs.backend.domain.Handle;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.lavalog.LavaLog;
import moe.ofs.backend.request.BaseRequest;
import moe.ofs.backend.request.Resolvable;

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
