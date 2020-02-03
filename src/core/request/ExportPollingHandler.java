package core.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.box.BoxOfExportUnit;
import core.object.ExportObject;
import core.request.export.ExportFillerRequest;
import core.request.export.ExportObjectDataRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ExportPollingHandler extends PollingHandler {

    private static ExportPollingHandler instance;

    protected boolean isRequestDone = true;
    protected List<ExportObject> list = new ArrayList<>();

    private ExportPollingHandler() {
        super(PollEnv.EXPORT);
        init();
    }

    public synchronized static ExportPollingHandler getInstance() {
        if (instance == null) {
            instance = new ExportPollingHandler();
        }
        return instance;
    }

    private void init() {
        int port = getPort();
        Gson gson = new Gson();
        String json;
        List<JsonRpcRequest> container = new ArrayList<>();

        ExportFillerRequest filler = new ExportFillerRequest();
        container.add(filler.toJsonRpcCall());
        json = gson.toJson(container);

        try {
            RequestHandler.sendAndGet(port, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void poll() {
        int port = getPort();
        Gson gson = new Gson();

        String json;

        flipCount++;
        if (flipCount >= 20 && isRequestDone) {

            flipCount = 0;
            ExportObjectDataRequest request = new ExportObjectDataRequest();
            List<JsonRpcRequest> container = new ArrayList<>();
            container.add(request.toJsonRpcCall());
            json = gson.toJson(container);
            isRequestDone = false;
        } else {
            ExportFillerRequest filler = new ExportFillerRequest();
            List<JsonRpcRequest> container = new ArrayList<>();
            container.add(filler.toJsonRpcCall());
            json = gson.toJson(container);
        }


        String s = "[]";
        try {
            s = RequestHandler.sendAndGet(port, json);
        } catch(IOException e) {
            e.printStackTrace();
        }

        if (!s.equals("[]")) {
//            System.out.println(s);

            Type jsonRpcResponseListType = new TypeToken<ArrayList<JsonRpcResponse<List<ExportObject>>>>() {}.getType();
            ArrayList<JsonRpcResponse<List<ExportObject>>> jsonRpcResponseList = gson.fromJson(s, jsonRpcResponseListType);

            List<ExportObject> exportObjectList =
                    jsonRpcResponseList.stream()
                            .flatMap(r -> r.getResult().getData().stream()).collect(Collectors.toList());

            list.addAll(exportObjectList);

            Optional<JsonRpcResponse<List<ExportObject>>> optional = jsonRpcResponseList.stream().findAny();
            if(optional.isPresent()) {
                JsonRpcResponse<List<ExportObject>> response = optional.get();
                if(list.size() == response.getResult().getTotal()) {
                    BoxOfExportUnit.observeAll(list);

                    isRequestDone = true;
                    list.clear();
                }
            }
        }
    }
}
