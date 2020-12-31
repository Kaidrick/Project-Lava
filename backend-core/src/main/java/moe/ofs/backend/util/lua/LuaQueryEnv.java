package moe.ofs.backend.util.lua;

public enum LuaQueryEnv {
    MISSION_SCRIPTING("server", 0),
    SERVER_CONTROL("debug", 1),
    EXPORT("export", 2),
    TRIGGER("mission", 3),
    SIM_CONFIG("config", 4);

    private String env;
    private int type;

    public int getType() {
        return type;
    }

    public String getEnv() {
        return env;
    }

    LuaQueryEnv(String env, int type) {
        this.env = env;
        this.type = type;
    }
}
