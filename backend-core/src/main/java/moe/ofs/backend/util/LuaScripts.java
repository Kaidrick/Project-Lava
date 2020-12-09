package moe.ofs.backend.util;

import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    // short hand methods for sending requests dcs lua env
    public static ServerDataRequest request(RequestToServer.State state, String luaString) {
        return ((ServerDataRequest) requestTransmissionService
                .send(new ServerDataRequest(state, luaString)));
    }

    public static String query(RequestToServer.State state, String luaString) {
        return request(state, luaString).get();
    }

    public static Object queryForObject(RequestToServer.State state, String luaString, Class<?> tClass) {
        return request(state, luaString).getAs(tClass);
    }

    public static List<?> queryForList(RequestToServer.State state, String luaString, Class<?> tClass) {
        Type type = TypeToken.getParameterized(ArrayList.class, tClass).getType();
        return request(state, luaString).getAs(type);
    }

    public static Set<?> queryForSet(RequestToServer.State state, String luaString, Class<?> tClass) {
        Type type = TypeToken.getParameterized(Set.class, tClass).getType();
        return request(state, luaString).getAs(type);
    }

    // FIXME: need further testing; also, is this really useful?
    public static Map<?, ?> queryForMap(RequestToServer.State state, String luaString, Class<?> tClass) {
        return request(state, luaString).getAs(Map.class);
    }
}
