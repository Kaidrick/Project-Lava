package moe.ofs.backend.connector.request;

import moe.ofs.backend.domain.connector.Level;
import moe.ofs.backend.connector.lua.LuaQueryEnv;

public abstract class DataRequest extends BaseRequest {

    protected LuaQueryEnv state;

    public DataRequest(Level level) {
        super(level);
    }
}
