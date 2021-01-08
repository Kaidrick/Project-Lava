package moe.ofs.backend.connector.util;

import moe.ofs.backend.connector.lua.LuaQueryEnv;
import moe.ofs.backend.connector.request.BaseRequest;
import moe.ofs.backend.connector.response.LuaResponse;
import moe.ofs.backend.connector.request.export.ExportDataRequest;
import moe.ofs.backend.connector.request.server.ServerDataRequest;
import moe.ofs.backend.connector.services.RequestTransmissionService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

public class LuaScripts {

    private static RequestTransmissionService requestTransmissionService;

    public RequestTransmissionService getRequestTransmissionService() {
        return requestTransmissionService;
    }

    public static void setRequestTransmissionService(RequestTransmissionService requestTransmissionService) {
        LuaScripts.requestTransmissionService = requestTransmissionService;
    }

    public static String load(String scriptName) {

        try(InputStream in = LuaScripts.class.getClassLoader().getResourceAsStream("scripts/" + scriptName)) {
            assert in != null;
            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))) {
                return bufferedReader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Script Not Found: " + scriptName);
        }
    }

    public static String safeLoad(String scriptName) {
        return injectSafe(load(scriptName));
    }

    public static String loadAndPrepare(String scriptName, Object... values) {
        try(InputStream in = LuaScripts.class.getClassLoader().getResourceAsStream("scripts/" + scriptName)) {
            assert in != null;
            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))) {
                String luaString = bufferedReader.lines().collect(Collectors.joining("\n"));
                return String.format(luaString, values);
            }
        } catch (IOException e) {
            throw new RuntimeException("Script Not Found: " + scriptName);
        }
    }

    public static String safeLoadAndPrepare(String scriptName, Object... values) {
        return injectSafe(loadAndPrepare(scriptName, values));
    }

    public static String injectSafe(String luaCode) {
        return luaCode.replaceAll("(?<!['\"])%[A-z](?!['\"])", "nil");
    }

    // short hand methods for sending requests dcs lua env
    public static LuaResponse request(LuaQueryEnv state, String luaString) {
        if (LuaQueryEnv.EXPORT.equals(state)) {
            return (LuaResponse) requestTransmissionService.send(
                    new ExportDataRequest(luaString)
            );
        }

        return (LuaResponse) requestTransmissionService
                .send(new ServerDataRequest(state, luaString));
    }

    public static LuaResponse requestWithFile(LuaQueryEnv state, String pathFromScripts, Object... args) {
        String luaString = loadAndPrepare(pathFromScripts, args);
        BaseRequest baseRequest = LuaQueryEnv.EXPORT.equals(state) ?
                new ExportDataRequest(luaString) : new ServerDataRequest(state, luaString);
        return (LuaResponse) requestTransmissionService
                .send(baseRequest);
    }

    public static String query(LuaQueryEnv state, String luaString) {
        return request(state, luaString).get();
    }

    // FIXME: need further testing; also, is this really useful?
    public static Map<?, ?> queryForMap(LuaQueryEnv state, String luaString, Class<?> tClass) {
        return request(state, luaString).getAs(Map.class);
    }
}
