package core.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.object.ExportObject;
import core.request.export.ExportObjectDataRequest;
import core.request.export.ExportFillerRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ExportPollingHandler extends PollingHandler {

    private static ExportPollingHandler instance;

    protected boolean isRequestDone = true;
    protected int batchCount = 0;
    protected List<ExportObject> groups = new ArrayList<>();

    private ExportPollingHandler() {
        super(PollEnv.EXPORT);
    }

    public synchronized static ExportPollingHandler getInstance() {
        if (instance == null) {
            instance = new ExportPollingHandler();
        }
        return instance;
    }

    public void poll() throws IOException {
        int port = getPort();

        Gson gson = new Gson();

        String json;

//        TestExportEmptyRequest filler = new TestExportEmptyRequest();
//        filler.prepareParameters();
//        List<RequestToExportAsync> container = new ArrayList<>();
//        container.add(filler);
//        json = gson.toJson(container);
//        System.out.println(json);
//        String s = SendManager.sendAndGet(port, json);


        flipCount++;
        if (flipCount >= 5 && isRequestDone) {

            flipCount = 0;
            ExportObjectDataRequest request = new ExportObjectDataRequest();
            request.prepareParameters();
            List<ExportObjectDataRequest> container = new ArrayList<>();
            container.add(request);
            json = gson.toJson(container);
            isRequestDone = false;
        } else {
//            System.out.println("flipCount = " + flipCount);
            ExportFillerRequest filler = new ExportFillerRequest();
            filler.prepareParameters();
            List<RequestToExportAsync> container = new ArrayList<>();
            container.add(filler);
            json = gson.toJson(container);
        }

//        System.out.println(json);

        // TODO: send request iff previous polling request has been completed
        String s = RequestHandler.sendAndGet(port, json);

//        if (!s.equals("[]"))
//            System.out.println(s);

//        this.isRequestDone = true;
//        this.batchCount = 0;

        Type keyValuePairsType = new TypeToken<ArrayList<ExportPollResult>>() {}.getType();
        ArrayList<ExportPollResult> keyValuePairsList = gson.fromJson(s, keyValuePairsType);


        List<ExportObject> exportObjectList =
                keyValuePairsList.stream()
                        .flatMap(r -> r.getResult().stream()).collect(Collectors.toList());

        groups.addAll(exportObjectList);

        // check if all result added up to the provided count
        Optional<ExportPollResult> resultOptional = keyValuePairsList.stream().findAny();
        if(resultOptional.isPresent()) {
            int dataCount = resultOptional.get().getTotal();
            if(groups.size() == dataCount) {

//                System.out.println("done!" + ++batchCount);

                // let do some data testing
//
                groups.parallelStream()
                        .collect(Collectors.groupingBy(ExportObject::getName, Collectors.counting()))
                        .forEach((typeName, typeCount) -> System.out.println(typeName + "=" + typeCount));

                this.isRequestDone = true;
                groups.clear();
            }
        }


    }
}
