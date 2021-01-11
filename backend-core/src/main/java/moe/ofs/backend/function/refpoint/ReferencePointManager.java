package moe.ofs.backend.function.refpoint;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.domain.dcs.theater.ReferencePoint;
import moe.ofs.backend.connector.request.server.ServerDataRequest;
import moe.ofs.backend.connector.services.RequestTransmissionService;
import moe.ofs.backend.connector.util.LuaScripts;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

@Component
public class ReferencePointManager {
    private Gson gson = new Gson();

    private final RequestTransmissionService requestTransmissionService;

    public ReferencePointManager(RequestTransmissionService requestTransmissionService) {
        this.requestTransmissionService = requestTransmissionService;
    }

    // add ref point to dcs
    public void addReferencePoint(ReferencePoint point) {
        //
    }

    // get ref point from dcs
    public List<ReferencePoint> getAll() {
        String luaString = LuaScripts.loadAndPrepare("refpoints/get_coalition_ref_points.lua", 2);

        String s = ((ServerDataRequest) requestTransmissionService.send(
                new ServerDataRequest(luaString))).get();

        Type referencePointListType = new TypeToken<List<ReferencePoint>>() {}.getType();
        return gson.fromJson(s, referencePointListType);
    }

}
