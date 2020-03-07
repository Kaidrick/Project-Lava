package moe.ofs.backend.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.domain.ExportObjectWrapper;
import moe.ofs.backend.request.export.ExportFillerRequest;
import moe.ofs.backend.request.export.ExportObjectDataRequest;
import moe.ofs.backend.services.ExportObjectService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public final class ExportPollHandler extends PollHandler {
    protected boolean isRequestDone = true;
    protected List<ExportObject> list = new ArrayList<>();

    private final ExportObjectService exportObjectService;

    public ExportPollHandler(ExportObjectService exportObjectService) {
        super(PollEnv.EXPORT);

        this.exportObjectService = exportObjectService;
    }

    // TODO --> refactor PollHandler abstract class
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

                    exportObjectService.cycle(list);

                    isRequestDone = true;
                    list.clear();
                }
            }
        }
    }
}
