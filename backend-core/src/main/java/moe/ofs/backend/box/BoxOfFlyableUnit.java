package moe.ofs.backend.box;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.object.FlyableUnit;
import moe.ofs.backend.util.Logger;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.request.BaseRequest;
import moe.ofs.backend.request.JsonRpcRequest;
import moe.ofs.backend.request.JsonRpcResponse;
import moe.ofs.backend.request.RequestHandler;
import moe.ofs.backend.request.server.ServerExecRequest;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public final class BoxOfFlyableUnit {
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

                Logger.log(box.size() + " flyable units data collected from DCS mission env.");
                box.forEach((name, data) -> System.out.println(data));
            }
        );
    }

    public static void init() {
        box.clear();

        String wholeText = LuaScripts.load("map_playable.lua");

        try {
            ServerExecRequest serverExecRequest = new ServerExecRequest(wholeText);

            ArrayList<JsonRpcRequest> wrapList = new ArrayList<>();
            wrapList.add(serverExecRequest.toJsonRpcCall());
            RequestHandler.sendAndGet(BaseRequest.Level.SERVER.getPort(), new Gson().toJson(wrapList));
            String playableJson = RequestHandler.sendAndGet(
                    BaseRequest.Level.SERVER.getPort(), "");  // TODO --> make proper jsonrpc

            BoxOfFlyableUnit.parse(playableJson);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static int getGroupIdByName(String groupName) {
        Optional<Map.Entry<String, FlyableUnit>> optional =
                box.entrySet().stream()
                        .filter(e -> e.getValue().getGroup_name().equals(groupName))
                        .findAny();
        return optional.orElseThrow(() -> new RuntimeException("Group name does not exist."))
                .getValue().getGroup_id();
    }
}
