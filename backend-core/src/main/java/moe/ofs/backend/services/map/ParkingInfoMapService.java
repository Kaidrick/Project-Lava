package moe.ofs.backend.services.map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.object.ParkingInfo;
import moe.ofs.backend.request.JsonRpcResponse;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.services.ParkingInfoService;
import moe.ofs.backend.util.ConnectionManager;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingInfoMapService extends AbstractMapService<ParkingInfo> implements ParkingInfoService {

    private String theater;

    private final ConnectionManager connectionManager;

    public ParkingInfoMapService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Optional<ParkingInfo> getParking(int airdromeId, int parkingId) {
        return findAll().parallelStream()
                .filter(p -> p.getAirdromeId() == airdromeId && p.getParkingId() == parkingId)
                .findAny();
    }

    @Override
    public List<ParkingInfo> getAllParking() {
        return new ArrayList<>(findAll());
    }

    @Override
    public void dispose() {
        deleteAll();
    }

    @Override
    @SuppressWarnings("unchecked")  // de-serialized object cannot be anything other than List of ParkingInfo
    public void loadData() {
        // get theater name
        String luaString = LuaScripts.load("get_map_theater_name.lua");

        try {
            ServerDataRequest serverExecRequest = new ServerDataRequest(luaString);


            connectionManager.fastPackThenSendAndCheck(serverExecRequest);
            String theaterDataJson = connectionManager.fastPackThenSendAndGet(serverExecRequest);

            Gson gson = new Gson();
            Type jsonRpcResponseListType = new TypeToken<ArrayList<JsonRpcResponse<String>>>(){}.getType();
            List<JsonRpcResponse<String>> jsonRpcResponseList =
                    gson.fromJson(theaterDataJson, jsonRpcResponseListType);

            jsonRpcResponseList.stream().findAny().ifPresent(
                    r -> {
                        String dataString = r.getResult().getData();
                        try {
                            theater = gson.fromJson(dataString, String.class);
                            InputStream inputStream = ParkingInfoMapService.class
                                    .getResourceAsStream(String.format("/data/%s.apron", theater));
                            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                            Object object = objectInputStream.readObject();
                            objectInputStream.close();

                            if(object instanceof ArrayList) {
                                List<ParkingInfo> list = (ArrayList<ParkingInfo>) object;
                                list.forEach(this::save);
                            }

                        } catch (JsonSyntaxException | IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                            System.out.println("bad data -> " + gson.fromJson(dataString, LinkedTreeMap.class));
                        }
                    }
            );

        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }
}
