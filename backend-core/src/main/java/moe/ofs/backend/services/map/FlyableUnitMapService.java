package moe.ofs.backend.services.map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.object.FlyableUnit;
import moe.ofs.backend.request.*;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.services.FlyableUnitService;
import moe.ofs.backend.util.Logger;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FlyableUnitMapService extends AbstractMapService<FlyableUnit> implements FlyableUnitService {

    // protected map for FlyableUnit in AbstractMapService

    @Override
    public Optional<FlyableUnit> findByUnitName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<FlyableUnit> findByUnitId(String idString) {

        // for shared cockpit aircraft such as Huey and Tomcat,
        // idString could be 1271 or 1271_n, where n is an integer

        // TODO -- what's wrong with this code?

        int id;
        if(idString.contains("_")) {  // multi-seat aircraft slot
            id = Integer.parseInt(idString.substring(0, idString.indexOf("_")));
        } else {
            if(idString.equals("")) {  // observer slot
                return Optional.empty();
            } else {
                id = Integer.parseInt(idString);
            }
        }

        return map.values().stream()
                .filter(e -> e.getUnit_id() == id)
                .findAny();
    }

    @Override
    public Optional<FlyableUnit> findByUnitId(Long id) {
        return map.values().stream()
                .filter(e -> e.getUnit_id() == id)
                .findAny();
    }

    @Override
    public Optional<Integer> findGroupIdByName(String name) {
        Optional<Map.Entry<Long, FlyableUnit>> optional =
                map.entrySet().stream()
                        .filter(e -> e.getValue().getGroup_name().equals(name))
                        .findAny();
        return optional.map(stringFlyableUnitEntry -> stringFlyableUnitEntry.getValue().getGroup_id());
    }

    @Override
    public void dispose() {
        map.clear();
    }

    @Override
    public void loadData() {
        String wholeText = LuaScripts.load("map_playable.lua");

        try {
            ServerExecRequest serverExecRequest = new ServerExecRequest(wholeText);

            ArrayList<JsonRpcRequest> wrapList = new ArrayList<>();
            wrapList.add(serverExecRequest.toJsonRpcCall());
            RequestHandler.sendAndGet(Level.SERVER.getPort(), new Gson().toJson(wrapList));
            String playableJson = RequestHandler.sendAndGet(
                    Level.SERVER.getPort(), "");  // TODO --> make proper jsonrpc

            parse(playableJson);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void parse(String playableJsonString) {
        Gson gson = new Gson();
        Type jsonRpcResponseListType = new TypeToken<ArrayList<JsonRpcResponse<String>>>(){}.getType();
        List<JsonRpcResponse<String>> jsonRpcResponseList =
                gson.fromJson(playableJsonString, jsonRpcResponseListType);

        jsonRpcResponseList.stream().findAny().ifPresent(
                r -> {
                    String dataString = r.getResult().getData();
                    Type mapUnitNameDataType = new TypeToken<Map<String, FlyableUnit>>(){}.getType();
                    Map<String, FlyableUnit> flyableUnitMap = gson.fromJson(dataString, mapUnitNameDataType);
                    flyableUnitMap.values().forEach(this::save);

                    Logger.log(this.findAll().size() + " flyable units data collected from DCS mission env.");
                    flyableUnitMap.forEach((name, data) -> System.out.println(data));
                }
        );
    }
}
