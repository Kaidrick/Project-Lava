package moe.ofs.backend.hookinterceptor;

import java.io.IOException;
import java.util.List;

public abstract class AbstractHookInterceptorProcessService
        <T extends HookProcessEntity, D extends HookInterceptorDefinition>
        implements HookInterceptorProcessService<T, D> {

    @Override
    public void createHook(String name, HookType hookType) {

    }

    @Override
    public List<T> poll() throws IOException {
        return null;
    }

    @Override
    public void addDefinition(D definition) {

    }

    @Override
    public void removeDefinition(D definition) {

    }
}
