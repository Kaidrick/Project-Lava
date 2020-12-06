package moe.ofs.backend.hookinterceptor;

import java.io.IOException;
import java.util.List;

public interface HookInterceptorProcessService<T extends AbstractHookProcessEntity> {
    List<T> poll() throws IOException;

    void addDefinition(AbstractHookInterceptorDefinition<T> definition);

    void removeDefinition(AbstractHookInterceptorDefinition<T> definition);
}
