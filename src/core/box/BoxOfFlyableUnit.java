package core.box;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.object.FlyableUnit;
import core.request.*;
import core.request.JsonRpcRequest;
import core.request.server.ServerExecRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BoxOfFlyableUnit {
    public static Map<String, FlyableUnit> box = new HashMap<>();

    private static void parse(String playableJsonString) {
        Gson gson = new Gson();
        Type jsonRpcResponseListType = new TypeToken<ArrayList<JsonRpcResponse<String>>>(){}.getType();
        List<JsonRpcResponse<String>> jsonRpcResponseList =
                gson.fromJson(playableJsonString, jsonRpcResponseListType);

        jsonRpcResponseList.stream().findAny().ifPresent(
            r -> {
                String dataString = r.getResult().getData();
                Type mapUnitNameDataType = new TypeToken<Map<String, FlyableUnit>>(){}.getType();
                Map<String, FlyableUnit> flyableUnitMap = gson.fromJson(dataString, mapUnitNameDataType);
                box.putAll(flyableUnitMap);
            }
        );
    }

    public static void init() throws IOException {
        // map all playable units from env
        // send init request to lua
        Path pathMapPlayable = Paths.get("src/core/request/scripts/map_playable.lua");
        BufferedReader bufferedReader = Files.newBufferedReader(pathMapPlayable);
        Optional<String> wholeText = bufferedReader.lines().reduce((s1, s2) -> s1 + "\n" + s2);

        ServerExecRequest serverExecRequest = new ServerExecRequest(wholeText.orElseThrow(
                () -> new RuntimeException("Error accessing script: " + pathMapPlayable.toString())));

        ArrayList<JsonRpcRequest> wrapList = new ArrayList<>();
        wrapList.add(serverExecRequest.toJsonRpcCall());
//        serverExecRequest.prepareParameters();
        RequestHandler.sendAndGet(BaseRequest.Level.SERVER.getPort(), new Gson().toJson(wrapList));
        String playableJson = RequestHandler.sendAndGet(
                BaseRequest.Level.SERVER.getPort(), "");  // TODO --> make proper jsonrpc

//        System.out.println(playableJson);
        BoxOfFlyableUnit.parse(playableJson);
    }
}
