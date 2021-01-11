package moe.ofs.backend.services.mizdb;

import moe.ofs.backend.connector.lua.LuaQueryEnv;

public class SimpleKeyValueStorage<T> extends AbstractKeyValueStorage<T> {

    public SimpleKeyValueStorage(String name, LuaQueryEnv env) {
        super(name, env);
    }

    @Override
    public String getRepositoryName() {
        return name;
    }


}
