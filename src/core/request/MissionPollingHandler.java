package core.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.object.Unit;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MissionPollingHandler extends PollingHandler {

    private static MissionPollingHandler instance;

    private MissionPollingHandler() {
        super(PollEnv.MISSION);
    }

    protected List<Unit> units = new ArrayList<>();

    public synchronized static MissionPollingHandler getInstance() {
        if (instance == null) {
            instance = new MissionPollingHandler();
        }
        return instance;
    }

    public void poll() throws IOException {
        int port = getPort();

        Gson gson = new Gson();

        String json;

        flipCount++;
        if(flipCount >= 100 && isRequestDone) {
            flipCount = 0;
            QueryGroupDataRequest request = new QueryGroupDataRequest(1);
            request.prepareParameters();
            List<QueryGroupDataRequest> container = new ArrayList<>();
            container.add(request);
            json = gson.toJson(container);
            isRequestDone = false;
        } else {
            json = "";
        }

//        System.out.println(json);

        // TODO: send request iff previous polling request has been completed
        String s = RequestHandler.sendAndGet(port, json);

//        System.out.println(s);

        if(!s.equals("[]"))
            System.out.println(s);

        // parse json into list of group objects, and generate a hashmap of runtime id key and object value?
        Type keyValuePairsType = new TypeToken<ArrayList<MissionPollResult>>() {}.getType();
        ArrayList<MissionPollResult> keyValuePairsList = gson.fromJson(s, keyValuePairsType);

        long size = keyValuePairsList.stream()
                .flatMap(r -> r.getResult().stream()).peek(units::add).count();  // bad practice but...

        batchCount += size;

        Optional<MissionPollResult> missionPollResultOptional = keyValuePairsList.stream().findAny();
        if(missionPollResultOptional.isPresent()) {
            MissionPollResult mpr = missionPollResultOptional.get();
            if (batchCount == mpr.getTotal()) {
                this.isRequestDone = true;
                this.batchCount = 0;

//                if(!groups.toString().equals("[]"))
//                    System.out.println(groups);

                units.forEach(u -> System.out.println(u.getName()));

                // parse groups into a hashmap?
                // find player controlled units
                units.clear();
            }
        }
    }
}
