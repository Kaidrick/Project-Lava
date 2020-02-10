package core.box;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.LuaScripts;
import core.object.Parking;
import core.request.BaseRequest;
import core.request.JsonRpcRequest;
import core.request.JsonRpcResponse;
import core.request.RequestHandler;
import core.request.server.ServerDataRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class BoxOfParking {
    public static List<Parking> box = new ArrayList<>();

    @SuppressWarnings("unchecked")
    private static void loadData(String theaterName) throws IOException, ClassNotFoundException {
        box.clear();

        InputStream inputStream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(String.format("core/data/%s.apron", theaterName));
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();

        if(object instanceof ArrayList) {
            List<Parking> list = (ArrayList<Parking>) object;
            box.addAll(list);
        }
    }

    private static void parse(String playableJsonString) {
        Gson gson = new Gson();
        Type jsonRpcResponseListType = new TypeToken<ArrayList<JsonRpcResponse<String>>>(){}.getType();
        List<JsonRpcResponse<String>> jsonRpcResponseList =
                gson.fromJson(playableJsonString, jsonRpcResponseListType);

        jsonRpcResponseList.stream().findAny().ifPresent(
                r -> {
                    String dataString = r.getResult().getData();
                    String theater = gson.fromJson(dataString, String.class);
                    try {
                        loadData(theater);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public static void init() {
        String luaString = LuaScripts.load("get_map_theater_name.lua");

        try {
            ServerDataRequest serverExecRequest = new ServerDataRequest(luaString);

            ArrayList<JsonRpcRequest> wrapList = new ArrayList<>();
            wrapList.add(serverExecRequest.toJsonRpcCall());
            RequestHandler.sendAndGet(BaseRequest.Level.SERVER.getPort(), new Gson().toJson(wrapList));
            String theaterDataJson = RequestHandler.sendAndGet(
                    BaseRequest.Level.SERVER.getPort(), "");  // TODO --> make proper jsonrpc
            parse(theaterDataJson);

        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }


//        String theaterName = ((ServerDataRequest) new ServerDataRequest(luaString).send()).get();
//        try {
//            loadData(theaterName);
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    public static Parking get(int airdromeId, int parkingId) {
        Optional<Parking> parkingOptional = box.parallelStream().filter(p -> p.getAirdromeId() == airdromeId && p.getId() == parkingId)
                .findAny();
        return parkingOptional.orElse(null);
    }
}
