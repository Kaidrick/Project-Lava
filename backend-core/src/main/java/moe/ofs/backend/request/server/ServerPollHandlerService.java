package moe.ofs.backend.request.server;

import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.request.AbstractPollHandlerService;
import moe.ofs.backend.services.UpdatableService;
import moe.ofs.backend.util.GenericClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("playerInfo")
public final class ServerPollHandlerService extends AbstractPollHandlerService<PlayerInfo> {

    @Autowired
    public ServerPollHandlerService(UpdatableService<PlayerInfo> service) {
        super(service);

        setGeneric(new GenericClass<>(PlayerInfo.class));
        setFlipThreshold(20);
    }

}
