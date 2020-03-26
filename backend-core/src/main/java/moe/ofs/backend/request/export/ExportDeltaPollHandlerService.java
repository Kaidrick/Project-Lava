package moe.ofs.backend.request.export;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.domain.LuaState;
import moe.ofs.backend.request.*;
import moe.ofs.backend.services.UpdatableService;
import moe.ofs.backend.util.ConnectionManager;
import moe.ofs.backend.util.GenericClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service("exportObjectDelta")
public final class ExportDeltaPollHandlerService implements PollHandlerService {

    protected List<DataUpdateBundle> list;

    protected int flipCount;

    protected int flipThreshold = 20;

    protected boolean requestCompleted;

    protected final UpdatableService<ExportObject> service;

    protected GenericClass<ExportObject> generic;

    protected Level level;

    public void setFlipThreshold(int flipThreshold) {
        this.flipThreshold = flipThreshold;
    }

    public void setGeneric(GenericClass<ExportObject> generic) {
        this.generic = generic;
        this.level = generic.getType().getAnnotation(LuaState.class).value();
    }

    @Autowired
    public ExportDeltaPollHandlerService(UpdatableService<ExportObject> service) {
        this.service = service;

        list = new ArrayList<>();

        setGeneric(new GenericClass<>(ExportObject.class));
        setFlipThreshold(20);
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

//        if(!s.equals("[]"))
//            System.out.println(s);

        if(s != null) {
            try {
                List<JsonRpcResponse<List<DataUpdateBundle>>> jsonRpcResponseList =
                        ConnectionManager.parseJsonResponse(s, DataUpdateBundle.class);

                List<DataUpdateBundle> bundleList = ConnectionManager.flattenResponse(jsonRpcResponseList);
                list.addAll(bundleList);

                jsonRpcResponseList.forEach(r -> {
                    if(r.getResult() != null && r.getResult().isIs_tail()) {
                        list.forEach(bundle -> {
                            switch (bundle.getAction()) {
                                case "create":
                                    service.add(bundle.getData());
                                    break;
                                case "update":
                                    service.update(bundle.getData());
                                    break;
                                case "delete":
                                    service.remove(bundle.getData());
                                    break;
                                default:
                                    throw new UnsupportedOperationException();
                            }
                        });

                        requestCompleted = true;
                        list.clear();
                    }
                });
//            }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception json string -> " + s);
            }
        }
    }

    @Override
    public void init() {
        requestCompleted = true;
        list.clear();
    }
}
