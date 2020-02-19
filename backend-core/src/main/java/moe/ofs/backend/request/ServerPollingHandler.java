package moe.ofs.backend.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.box.BoxOfPlayerInfo;
import moe.ofs.backend.object.PlayerInfo;
import moe.ofs.backend.request.server.ServerFillerRequest;
import moe.ofs.backend.request.server.ServerPlayerInfoRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


public final class ServerPollingHandler extends PollingHandler {

    private static ServerPollingHandler instance;

    private ServerPollingHandler() {
        super(PollEnv.SERVER);
//        init();
    }

    private List<PlayerInfo> list = new ArrayList<>();

    public synchronized static ServerPollingHandler getInstance() {
        if (instance == null) {
            instance = new ServerPollingHandler();
        }
        return instance;
    }

    public void init() {
        int port = getPort();
        Gson gson = new Gson();
        String json;
        List<JsonRpcRequest> container = new ArrayList<>();

        ServerFillerRequest filler = new ServerFillerRequest();
        container.add(filler.toJsonRpcCall());
        json = gson.toJson(container);

        try {
            RequestHandler.sendAndGet(port, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void poll() throws IOException {
        int port = getPort();
        Gson gson = new Gson();
        String json;

        // implement poll method
        flipCount++;
        if (flipCount >= 10 && isRequestDone) {

            flipCount = 0;
            ServerPlayerInfoRequest request = new ServerPlayerInfoRequest();
//            request.prepareParameters();
            List<JsonRpcRequest> container = new ArrayList<>();
            container.add(request.toJsonRpcCall());
            json = gson.toJson(container);
            isRequestDone = false;
        } else {
//            System.out.println("flipCount = " + flipCount);
            ServerFillerRequest filler = new ServerFillerRequest();
//            filler.prepareParameters();
            List<JsonRpcRequest> container = new ArrayList<>();
            container.add(filler.toJsonRpcCall());
            json = gson.toJson(container);
        }

//        if(!json.equals("[]"))
//            System.out.println(json);

        // TODO: send request iff previous polling request has been completed
        String s = RequestHandler.sendAndGet(port, json);

        if (!s.equals("[]")) {
//            System.out.println(s);

            Type jsonRpcResponseListType = new TypeToken<List<JsonRpcResponse<List<PlayerInfo>>>>() {}.getType();
            List<JsonRpcResponse<List<PlayerInfo>>> jsonRpcResponseList = gson.fromJson(s, jsonRpcResponseListType);

//            System.out.println(jsonRpcResponseList.get(0).getResult().getData());

            List<PlayerInfo> playerInfoList =
                    jsonRpcResponseList.stream()
                            .flatMap(r -> r.getResult().getData().stream()).collect(Collectors.toList());

            // TODO --> check size, make map
            list.addAll(playerInfoList);

            Optional<JsonRpcResponse<List<PlayerInfo>>> optional = jsonRpcResponseList.stream().findAny();
            if(optional.isPresent()) {
                JsonRpcResponse<List<PlayerInfo>> response = optional.get();
                if(list.size() == response.getResult().getTotal()) {

                    Map<String, PlayerInfo> compareMap = list.stream()
                            .collect(Collectors.toMap(PlayerInfo::getName, Function.identity()));
                    BoxOfPlayerInfo.observeAll(compareMap);

                    isRequestDone = true;
                    list.clear();
                }
            }

//            jsonRpcResponseList.stream().findAny().ifPresent(
//                    r -> {
//                        list.addAll(r.getResult().getData());
//                        if (list.size() == r.getResult().getTotal()) {
//                            isRequestDone = true;
//
//                            Map<String, PlayerInfo> compareMap = list.stream()
//                                    .collect(Collectors.toMap(PlayerInfo::getName, Function.identity()));
//
//                            BoxOfPlayerInfo.observeAll(compareMap);
//
//                            list.clear();
//                        }
//                    }
//            );
        }
    }

    public static void main(String[] args) throws IOException {
        ServerPollingHandler serverPollingHandler = ServerPollingHandler.getInstance();

        while(true){
            serverPollingHandler.poll();
        }
    }
}
