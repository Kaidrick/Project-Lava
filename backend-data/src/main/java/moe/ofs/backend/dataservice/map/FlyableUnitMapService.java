package moe.ofs.backend.dataservice.map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import moe.ofs.backend.LavaLog;
//import moe.ofs.backend.lavalog.eventlogger.SpawnControlLogger;
import moe.ofs.backend.common.AbstractMapService;
import moe.ofs.backend.object.FlyableUnit;
import moe.ofs.backend.connector.ConnectionManager;
import moe.ofs.backend.connector.util.LuaScripts;
import moe.ofs.backend.connector.response.JsonRpcResponse;
import moe.ofs.backend.connector.request.server.ServerExecRequest;
import moe.ofs.backend.dataservice.FlyableUnitService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FlyableUnitMapService extends AbstractMapService<FlyableUnit> implements FlyableUnitService {

//    private final LavaLog.Logger logger = LavaLog.getLogger(SpawnControlLogger.class);

    // protected map for FlyableUnit in AbstractMapService

    private final ConnectionManager connectionManager;

    public FlyableUnitMapService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Optional<FlyableUnit> findByUnitName(String name) {
        return findAll().stream().filter(e -> e.getUnit_name().equals(name)).findAny();
    }

    @Override
    public Optional<FlyableUnit> findByUnitId(String idString) {

        if (idString == null) return Optional.empty();

        // for shared cockpit aircraft such as Huey and Tomcat,
        // idString could be 1271 or 1271_n, where n is an integer
        int id;
        if(idString.contains("_") && idString.split("_").length == 2) {  // multi-seat aircraft slot
            id = Integer.parseInt(idString.substring(0, idString.indexOf("_")));
        } else {
            if(idString.equals("") || idString.contains("instructor") ||
                idString.contains("forward_observer") || idString.contains("artillery_commander") ||
                idString.contains("observer")) {  // observer slot
                return Optional.empty();
            } else {
                id = Integer.parseInt(idString);
            }
        }

        return findAll().stream()
                .filter(e -> e.getUnit_id() == id)
                .findAny();
    }

    @Override
    public Optional<FlyableUnit> findByUnitId(Long id) {
        return findAll().stream()
                .filter(e -> e.getUnit_id() == id)
                .findAny();
    }

    @Override
    public Optional<Integer> findGroupIdByName(String name) {
        Optional<FlyableUnit> optional =
                findAll().stream()
                        .filter(e -> e.getGroup_name().equals(name))
                        .findAny();
        return optional.map(FlyableUnit::getGroup_id);
    }

    @Override
    public void dispose() {
        deleteAll();
    }

    @Override
    public void loadData() {
        String wholeText = LuaScripts.load("map_playable.lua");

        try {
            ServerExecRequest serverExecRequest = new ServerExecRequest(wholeText);

            connectionManager.fastPackThenSendAndGet(serverExecRequest);
            String playableJson = connectionManager.fastPackThenSendAndGet(serverExecRequest);
            // TODO --> make proper jsonrpc

            parse(playableJson);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void parse(String playableJsonString) {
        Gson gson = new Gson();
        Type jsonRpcResponseListType = new TypeToken<List<JsonRpcResponse<String>>>() {}.getType();
        List<JsonRpcResponse<String>> jsonRpcResponseList =
                gson.fromJson(playableJsonString, jsonRpcResponseListType);

        jsonRpcResponseList.stream().findAny().ifPresent(
                r -> {
                    String dataString = r.getResult().getData();
                    Type mapUnitNameDataType = new TypeToken<Map<String, FlyableUnit>>(){}.getType();
                    Map<String, FlyableUnit> flyableUnitMap = gson.fromJson(dataString, mapUnitNameDataType);
                    flyableUnitMap.values().forEach(this::save);

//                    logger.log(this.findAll().size() + " flyable units data collected from DCS mission env.");
//                    flyableUnitMap.forEach((name, data) -> logger.debug(data.toString()));
                }
        );
    }
}
