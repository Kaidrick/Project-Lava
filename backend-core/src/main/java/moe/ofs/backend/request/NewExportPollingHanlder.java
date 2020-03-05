package moe.ofs.backend.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.ControlPanelApplication;
import moe.ofs.backend.dataset.ExportUnitDataSet;
import moe.ofs.backend.object.ExportObject;
import moe.ofs.backend.object.ExportObjectWrapper;
import moe.ofs.backend.request.export.ExportFillerRequest;
import moe.ofs.backend.request.export.ExportObjectDataRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class NewExportPollingHanlder extends PollingHandler {
    protected boolean isRequestDone = true;
    protected List<ExportObject> list = new ArrayList<>();

    ExportUnitDataSet exportUnitDataSet = ControlPanelApplication.applicationContext.getBean(ExportUnitDataSet.class);

    public NewExportPollingHanlder() {
        super(PollEnv.EXPORT);
    }

    public void init() {
        isRequestDone = true;
        list.clear();

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

            Type jsonRpcResponseListType = new TypeToken<ArrayList<JsonRpcResponse<List<ExportObjectWrapper>>>>() {}.getType();
            ArrayList<JsonRpcResponse<List<ExportObjectWrapper>>> jsonRpcResponseList = gson.fromJson(s, jsonRpcResponseListType);

            List<ExportObject> exportObjectList =
                    jsonRpcResponseList.stream()
                            .flatMap(r -> r.getResult().getData().stream())
                            .map(ExportObject::new)
                            .collect(Collectors.toList());

            list.addAll(exportObjectList);

            Optional<JsonRpcResponse<List<ExportObjectWrapper>>> optional = jsonRpcResponseList.stream().findAny();
            if(optional.isPresent()) {
                JsonRpcResponse<List<ExportObjectWrapper>> response = optional.get();
                if(list.size() == response.getResult().getTotal()) {
                    exportUnitDataSet.cycle(list);

                    isRequestDone = true;
                    list.clear();
                }
            }
        }
    }
}
