package moe.ofs.backend.connector.lua;

import moe.ofs.backend.connector.response.LuaResponse;
import moe.ofs.backend.connector.util.LuaScripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class LuaScriptLoader {
    private final Class<?> initiatorClass;
    private final String resourceRootPath;

    public LuaScriptLoader(Class<?> initiatorClass, String resourceRootPath) {
        this.initiatorClass = initiatorClass;
        this.resourceRootPath = resourceRootPath;
    }

    @SuppressWarnings("unused")
    public LuaScriptLoader(Class<?> initiatorClass) {
        this(initiatorClass, "scripts");
    }

//    protected LuaScriptLoader() {
//        this(LuaScriptLoader.class);
//    }

    public String load(String scriptName) {

        try(InputStream in = initiatorClass.getClassLoader()
                .getResourceAsStream(resourceRootPath + "/" + scriptName)) {
            assert in != null;
            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))) {
                return bufferedReader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Script Not Found: " + scriptName);
        }
    }

    public String safeLoad(String scriptName) {
        return injectSafe(load(scriptName));
    }

    public String loadAndPrepare(String scriptName, Object... values) {
        try(InputStream in = initiatorClass.getClassLoader()
                .getResourceAsStream(resourceRootPath + "/" + scriptName)) {
            assert in != null;
            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))) {
                String luaString = bufferedReader.lines().collect(Collectors.joining("\n"));
                return String.format(luaString, values);
            }
        } catch (IOException e) {
            throw new RuntimeException("Script Not Found: " + scriptName);
        }
    }

    public String safeLoadAndPrepare(String scriptName, Object... values) {
        return injectSafe(loadAndPrepare(scriptName, values));
    }

    public String injectSafe(String luaCode) {
        return luaCode.replaceAll("(?<!['\"])%[A-z](?!['\"])", "nil");
    }

    // short hand methods for sending requests dcs lua env
    public LuaResponse request(LuaQueryEnv state, String luaString) {
        return LuaScripts.request(state, luaString);
    }

    public LuaResponse requestWithFile(LuaQueryEnv state, String pathFromScripts, Object... args) {
        return request(state, loadAndPrepare(pathFromScripts, args));
    }

    public String query(LuaQueryEnv state, String luaString) {
        return request(state, luaString).get();
    }
}
