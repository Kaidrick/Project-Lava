package moe.ofs.backend.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.request.server.ServerFillerRequest;
import moe.ofs.backend.request.server.ServerPlayerInfoRequest;
import moe.ofs.backend.services.PlayerInfoService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public final class ServerPollHandler extends PollHandler {
    protected List<PlayerInfo> list = new ArrayList<>();

    private final PlayerInfoService playerInfoService;

    public ServerPollHandler(PlayerInfoService playerInfoService) {
        super(PollEnv.SERVER);

        this.playerInfoService = playerInfoService;
    }

    public void init() {
        isRequestDone = true;
        list.clear();

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

        String s = RequestHandler.sendAndGet(port, json);

        if (!s.equals("[]")) {
//            System.out.println(s);

            Type jsonRpcResponseListType = new TypeToken<List<JsonRpcResponse<List<PlayerInfo>>>>() {}.getType();
            List<JsonRpcResponse<List<PlayerInfo>>> jsonRpcResponseList = gson.fromJson(s, jsonRpcResponseListType);

//            System.out.println(jsonRpcResponseList.get(0).getResult().getData());

            List<PlayerInfo> playerInfoList =
                    jsonRpcResponseList.stream()
                            .flatMap(r -> r.getResult().getData().stream()).collect(Collectors.toList());

            list.addAll(playerInfoList);

            Optional<JsonRpcResponse<List<PlayerInfo>>> optional = jsonRpcResponseList.stream().findAny();
            if(optional.isPresent()) {
                JsonRpcResponse<List<PlayerInfo>> response = optional.get();
                if(list.size() == response.getResult().getTotal()) {

//                    for (long i = 5; i < 20; i++) {
//                        PlayerInfo playerInfo = new PlayerInfo();
//                        playerInfo.setName("test" + i);
//                        playerInfo.setIpaddr("dfasdfsd" + i);
//                        playerInfo.setLang("cn");
//                        playerInfo.setPing((int) (i + 14));
//                        playerInfo.setSide(1);
//                        playerInfo.setSlot("12" + i);
//                        playerInfo.setUcid("2579384yhtfgn39845ygh94" + i);
//                        playerInfo.setStarted(true);
//                        list.add(playerInfo);
//                    }

                    playerInfoService.cycle(list);

                    isRequestDone = true;
                    list.clear();
                }
            }
        }
    }
}
