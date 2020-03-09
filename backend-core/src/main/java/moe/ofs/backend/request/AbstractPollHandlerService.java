package moe.ofs.backend.request;

import moe.ofs.backend.domain.BaseEntity;
import moe.ofs.backend.services.UpdatableService;
import moe.ofs.backend.util.ConnectionManager;
import moe.ofs.backend.util.GenericClass;
import moe.ofs.backend.util.LuaState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPollHandlerService<T extends BaseEntity> implements PollHandlerService {

    protected List<T> list;

    protected int flipCount;

    protected int flipThreshold = 20;

    protected boolean requestCompleted;

    protected final UpdatableService<T> service;

    protected GenericClass<T> generic;

    protected Level level;

    public void setFlipThreshold(int flipThreshold) {
        this.flipThreshold = flipThreshold;
    }

    public void setGeneric(GenericClass<T> generic) {
        this.generic = generic;
        this.level = generic.getType().getAnnotation(LuaState.class).value();
    }

    public AbstractPollHandlerService(UpdatableService<T> service) {
        this.list = new ArrayList<>();

        this.service = service;
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

        String s = ConnectionManager.fastPackThenSendAndGet(request);

        List<JsonRpcResponse<List<T>>> jsonRpcResponseList = ConnectionManager.parseJsonResponse(s, generic.getType());
        List<T> objectList = ConnectionManager.flattenResponse(jsonRpcResponseList);
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
