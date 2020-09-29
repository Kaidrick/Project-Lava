package moe.ofs.backend.util;

import com.google.gson.Gson;
import moe.ofs.backend.request.server.ServerDataRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class LuaScripts {
    public static String load(String scriptName) {

        try(InputStream in = LuaScripts.class.getClassLoader().getResourceAsStream("scripts/" + scriptName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))) {
            return bufferedReader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Script Not Found: " + scriptName);
        }
    }

    public static String loadAndPrepare(String scriptName, Object... values) {
        try(InputStream in = LuaScripts.class.getClassLoader().getResourceAsStream("scripts/" + scriptName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))) {
            String luaString = bufferedReader.lines().collect(Collectors.joining("\n"));
            return String.format(luaString, values);
        } catch (IOException e) {
            throw new RuntimeException("Script Not Found: " + scriptName);
        }
    }

    /**
     * Run lua string in mission environment if connection is established properly
     * @param luaString The lua string to be executed in the mission environment
     */
    public static String call(String luaString) {
        return ((ServerDataRequest) new ServerDataRequest(luaString).send()).get();
    }

    public static void run(String luaString) {
        new ServerDataRequest(luaString).send();
    }

    public static <T> String runAndThen(String luaString, Callable<T> callable) {
        // TODO -> try to convert to T and then return? is it possible?
        new ServerDataRequest(luaString)
                .addProcessable(s -> {
                    if(!s.equals("[]"))
                        System.out.println(s);

                    Gson gson = new Gson();

                    // the response string can be converted to a list for T, or map for T, or just T

                    // what kind of data need to be queried via lua?


                }).send();
        return null;
    }
}
