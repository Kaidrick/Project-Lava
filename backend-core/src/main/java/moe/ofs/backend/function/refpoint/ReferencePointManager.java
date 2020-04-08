package moe.ofs.backend.function.refpoint;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.object.map.ReferencePoint;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

@Component
public class ReferencePointManager {
    private Gson gson = new Gson();

    // add ref point to dcs
    public void addReferencePoint(ReferencePoint point) {
        //
    }


    // get ref point from dcs
    public List<ReferencePoint> getAll() {
        String luaString = LuaScripts.loadAndPrepare("refpoints/get_coalition_ref_points.lua", 2);
        String s = ((ServerDataRequest) new ServerDataRequest(luaString).send()).get();

        Type referencePointListType = new TypeToken<List<ReferencePoint>>() {}.getType();
        return gson.fromJson(s, referencePointListType);
    }

}
