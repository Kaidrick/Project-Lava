package core.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.box.BoxOfPlayerInfo;
import core.object.PlayerInfo;
import core.request.server.ServerFillerRequest;
import core.request.server.ServerPlayerInfoRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public final class ServerPollingHandler extends PollingHandler {

    private static ServerPollingHandler instance;

    private ServerPollingHandler() {
        super(PollEnv.SERVER);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<PlayerInfo> list = new ArrayList<>();

    public synchronized static ServerPollingHandler getInstance() {
        if (instance == null) {
            instance = new ServerPollingHandler();
        }
        return instance;
    }

    private void init() throws IOException {
        int port = getPort();
        Gson gson = new Gson();
        String json;
        ServerFillerRequest filler = new ServerFillerRequest();
//            filler.prepareParameters();
        List<JsonRpcRequest> container = new ArrayList<>();
        container.add(filler.toJsonRpcCall());
        json = gson.toJson(container);
//        System.out.println(json);
        String s = RequestHandler.sendAndGet(port, json);
        System.out.println("cleaning..." + s);
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

            jsonRpcResponseList.stream().findAny().ifPresent(
                    r -> {
                        list.addAll(r.getResult().getData());
                        if (list.size() == r.getResult().getTotal()) {
                            isRequestDone = true;

                            Map<String, PlayerInfo> compareMap = list.stream()
                                    .collect(Collectors.toMap(PlayerInfo::getName, Function.identity()));

                            BoxOfPlayerInfo.observeAll(compareMap);

                            list.clear();
                        }
                    }
            );
        }
    }

    public static void main(String[] args) throws IOException {
        ServerPollingHandler serverPollingHandler = ServerPollingHandler.getInstance();

        while(true){
            serverPollingHandler.poll();
        }
    }
}
