package moe.ofs.backend.hookinterceptor;

import java.util.function.Consumer;

public abstract class AbstractHookInterceptorDefinition<T extends AbstractHookProcessEntity> {
    private String name;
    private Consumer<T> consumer;
}
