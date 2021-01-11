package moe.ofs.backend.pollservices;

import moe.ofs.backend.connector.ConnectionManager;
import moe.ofs.backend.common.GenericClass;
import moe.ofs.backend.connector.Connection;
import moe.ofs.backend.connector.RequestHandler;
import moe.ofs.backend.connector.request.BaseRequest;
import moe.ofs.backend.connector.request.FillerRequest;
import moe.ofs.backend.connector.request.PollRequest;
import moe.ofs.backend.connector.response.JsonRpcResponse;
import moe.ofs.backend.domain.dcs.poll.ExportObject;
import moe.ofs.backend.domain.connector.Level;
import moe.ofs.backend.domain.dcs.LuaState;
import moe.ofs.backend.common.UpdatableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("exportObjectDelta")
public final class ExportDeltaPollHandlerService implements PollHandlerService {

    protected List<DataUpdateBundle> list;

    private final RequestHandler requestHandler;

//    private ExecutorService executorService;

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
    public ExportDeltaPollHandlerService(RequestHandler requestHandler, UpdatableService<ExportObject> service) {
        this.requestHandler = requestHandler;
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

//                List<JsonRpcResponse<List<DcsExportObjectDataUpdateBundle>>> testResponseList =
//                        ConnectionManager.parseJsonResponse(s, DcsExportObjectDataUpdateBundle.class);

                List<DataUpdateBundle> bundleList = ConnectionManager.flattenResponse(jsonRpcResponseList);
                list.addAll(bundleList);

//                List<DcsExportObjectDataUpdateBundle> testBundleResponseList = ConnectionManager.flattenResponse(testResponseList);
//                System.out.println("testBundleResponseList.size() = " + testBundleResponseList.size());

                jsonRpcResponseList.forEach(r -> {
                    if(r.getResult() != null && r.getResult().isIs_tail()) {
                        Map<String, List<DataUpdateBundle>> map = list.parallelStream().unordered()
                                .collect(Collectors.groupingBy(DataUpdateBundle::getAction));

                        map.getOrDefault("create", Collections.emptyList()).forEach(dataUpdateBundle ->
                                service.add(dataUpdateBundle.getData()));

                        map.getOrDefault("update", Collections.emptyList()).forEach(dataUpdateBundle ->
                                service.update(dataUpdateBundle.getData()));

                        map.getOrDefault("delete", Collections.emptyList()).forEach(dataUpdateBundle ->
                                service.remove(dataUpdateBundle.getData()));

//                        list.parallelStream().forEach(bundle -> {
//                            switch (bundle.getAction()) {
//                                case "create":
////                                    executorService.submit(() -> service.add(bundle.getData()));
//                                    service.add(bundle.getData());
//                                    break;
//                                case "update":
////                                    executorService.submit(() -> service.update(bundle.getData()));
//                                    service.update(bundle.getData());
//                                    break;
//                                case "delete":
////                                    executorService.submit(() -> service.remove(bundle.getData()));
//                                    service.remove(bundle.getData());
//                                    break;
//                                default:
//                                    throw new UnsupportedOperationException();
//                            }
//                        });

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
