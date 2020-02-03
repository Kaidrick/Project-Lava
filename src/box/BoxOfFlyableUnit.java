package core.box;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.LuaScripts;
import core.object.FlyableUnit;
import core.request.BaseRequest;
import core.request.JsonRpcRequest;
import core.request.JsonRpcResponse;
import core.request.RequestHandler;
import core.request.server.ServerExecRequest;
import java.io.IOException;
import java.lang.reflect.Type;
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

                System.out.println(box.size() + " flyable units data collected from DCS mission env.");
            }
        );
    }

    public static void init() {
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

    public static int getGroupId(String groupName) {
        Optional<Map.Entry<String, FlyableUnit>> optional =
                box.entrySet().stream()
                        .filter(e -> e.getValue().getGroup_name().equals(groupName))
                        .findAny();
        return optional.orElseThrow(() -> new RuntimeException("Group name does not exist."))
                .getValue().getGroup_id();
    }
}
