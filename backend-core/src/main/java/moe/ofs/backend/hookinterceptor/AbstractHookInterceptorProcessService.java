package moe.ofs.backend.hookinterceptor;

import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.PlayerInfoService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractHookInterceptorProcessService<T extends AbstractHookProcessEntity>
        implements HookInterceptorProcessService<T> {

    protected List<AbstractHookInterceptorDefinition<T>> definitions = new CopyOnWriteArrayList<>();

    protected final RequestTransmissionService requestTransmissionService;
    protected final PlayerInfoService playerInfoService;

    public AbstractHookInterceptorProcessService(RequestTransmissionService requestTransmissionService,
                                                 PlayerInfoService playerInfoService) {
        this.requestTransmissionService = requestTransmissionService;
        this.playerInfoService = playerInfoService;
    }
    
    @Override
    public abstract List<T> poll() throws IOException;

    @Override
    public void addDefinition(AbstractHookInterceptorDefinition<T> definition) {
        definitions.add(definition);
    }

    @Override
    public void removeDefinition(AbstractHookInterceptorDefinition<T> definition) {
        definitions.remove(definition);
    }
}
