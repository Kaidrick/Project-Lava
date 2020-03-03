package moe.ofs.backend.util;

import moe.ofs.backend.ControlPanelApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class LuaScripts {
    public static String load(String scriptName) {

        try(InputStream in = ControlPanelApplication.class.getClassLoader().getResourceAsStream("scripts/" + scriptName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))) {
            return bufferedReader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Script Not Found: " + scriptName);
        }
    }

    public static String loadAndPrepare(String scriptName, Object... values) {
        try(InputStream in = ControlPanelApplication.class.getClassLoader().getResourceAsStream("scripts/" + scriptName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))) {
            String luaString = bufferedReader.lines().collect(Collectors.joining("\n"));
            return String.format(luaString, values);
        } catch (IOException e) {
            throw new RuntimeException("Script Not Found: " + scriptName);
        }
    }
}
