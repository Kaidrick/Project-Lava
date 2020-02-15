package ofs.backend;

import ofs.backend.BackendMain;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class LuaScripts {
    public static String load(String scriptName) {
        InputStream in = BackendMain.class.getClassLoader().getResourceAsStream("scripts/" + scriptName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        return bufferedReader.lines().collect(Collectors.joining("\n"));
    }

    public static void main(String[] args) {
        System.out.println(load("map_playable.lua"));
    }
}
