package moe.ofs.backend.request.export;

import moe.ofs.backend.connector.model.DcsExportObjectDataUpdateBundle;
import moe.ofs.backend.connector.services.ExportObjectDataService;
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
import java.util.List;

@Service("exportObjectDelta")
public final class ExportDeltaPollHandlerService implements PollHandlerService {

    protected List<DataUpdateBundle> list;

    private final RequestHandler requestHandler;

//    private ExecutorService executorService;

    private final ExportObjectDataService exportObjectDataService;

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
    public ExportDeltaPollHandlerService(RequestHandler requestHandler, ExportObjectDataService exportObjectDataService, UpdatableService<ExportObject> service) {
        this.requestHandler = requestHandler;
        this.exportObjectDataService = exportObjectDataService;
        this.service = service;

//        executorService = Executors.newCachedThreadPool();
//
//        // FIXME: shutdown
//        ControlPanelShutdownObservable observable = () -> {
//            executorService.shutdown();
//            executorService.shutdownNow();
//        };
//        observable.register();

        list = new ArrayList<>();

        setGeneric(new GenericClass<>(ExportObject.class));
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

//        String s = ConnectionManager.fastPackThenSendAndGet(request);

//        if(!s.equals("[]"))
//            System.out.println(s);

        if(s != null) {
            try {
                List<JsonRpcResponse<List<DataUpdateBundle>>> jsonRpcResponseList =
                        ConnectionManager.parseJsonResponse(s, DataUpdateBundle.class);

                List<JsonRpcResponse<List<DcsExportObjectDataUpdateBundle>>> testResponseList =
                        ConnectionManager.parseJsonResponse(s, DcsExportObjectDataUpdateBundle.class);

                List<DataUpdateBundle> bundleList = ConnectionManager.flattenResponse(jsonRpcResponseList);
                list.addAll(bundleList);

                List<DcsExportObjectDataUpdateBundle> testBundleResponseList = ConnectionManager.flattenResponse(testResponseList);
//                System.out.println("testBundleResponseList.size() = " + testBundleResponseList.size());

                jsonRpcResponseList.forEach(r -> {
                    if(r.getResult() != null && r.getResult().isIs_tail()) {
                        list.parallelStream().forEach(bundle -> {
                            switch (bundle.getAction()) {
                                case "create":
//                                    executorService.submit(() -> service.add(bundle.getData()));
                                    service.add(bundle.getData());
                                    break;
                                case "update":
//                                    executorService.submit(() -> service.update(bundle.getData()));
                                    service.update(bundle.getData());
                                    break;
                                case "delete":
//                                    executorService.submit(() -> service.remove(bundle.getData()));
                                    service.remove(bundle.getData());
                                    break;
                                default:
                                    throw new UnsupportedOperationException();
                            }
                        });

                        // add a lock so that next iter is run only after batch update finishes?

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
