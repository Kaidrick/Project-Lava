package moe.ofs.backend.request;

import moe.ofs.backend.domain.Level;
import moe.ofs.backend.util.lua.LuaQueryEnv;

public abstract class DataRequest extends BaseRequest {

    protected LuaQueryEnv state;

    public DataRequest(Level level) {
        super(level);
    }
}
