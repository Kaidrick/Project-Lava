package moe.ofs.backend.services.mizdb;

import moe.ofs.backend.util.lua.LuaQueryEnv;

public class SimpleDataStorage<T> extends AbstractDataStorage<T> {

    public SimpleDataStorage(String name, LuaQueryEnv env) {
        super(name, env);
    }

    @Override
    public String getRepositoryName() {
        return name;
    }
}
