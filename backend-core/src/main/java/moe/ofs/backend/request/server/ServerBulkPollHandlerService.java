package moe.ofs.backend.request.server;

import moe.ofs.backend.domain.Level;
import moe.ofs.backend.domain.LuaState;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.request.*;
import moe.ofs.backend.services.UpdatableService;
import moe.ofs.backend.util.ConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("playerInfoBulk")
public final class ServerBulkPollHandlerService implements PollHandlerService {

    private final RequestHandler requestHandler;

    protected List<PlayerInfo> list;

    protected int flipCount;

    protected int flipThreshold = 20;

    protected boolean requestCompleted;

    protected final UpdatableService<PlayerInfo> service;

    protected Level level;

    public void setFlipThreshold(int flipThreshold) {
        this.flipThreshold = flipThreshold;
    }

    @Autowired
    public ServerBulkPollHandlerService(RequestHandler requestHandler, UpdatableService<PlayerInfo> service) {
        this.requestHandler = requestHandler;
        this.service = service;

        this.level = PlayerInfo.class.getAnnotation(LuaState.class).value();

        list = new ArrayList<>();

        setFlipThreshold(5);
    }

    @Override
    public void poll() throws IOException {

        flipCount++;

        BaseRequest request;
        if (flipCount >= flipThreshold && requestCompleted) {

            flipCount = 0;

            request = new PollRequest(level);

            requestCompleted = false;

        } else {

            request = new FillerRequest(level);

        }

        Connection connection = requestHandler.getConnections().get(level);
        String s = connection.transmitAndReceive(ConnectionManager.fastPack(request));

//        System.out.println(s);
//        String s = ConnectionManager.fastPackThenSendAndGet(request);

        List<JsonRpcResponse<List<PlayerInfo>>> jsonRpcResponseList = ConnectionManager.parseJsonResponse(s, PlayerInfo.class);
        List<PlayerInfo> objectList = ConnectionManager.flattenResponse(jsonRpcResponseList);
        list.addAll(objectList);

        jsonRpcResponseList.stream()
                .findAny().ifPresent(r -> {
                    if(list.size() == r.getResult().getTotal()) {
                        service.cycle(list);
                        requestCompleted = true;
                        list.clear();
                    }
                });
    }

    @Override
    public void init() {
        requestCompleted = true;
        list.clear();
    }

}
