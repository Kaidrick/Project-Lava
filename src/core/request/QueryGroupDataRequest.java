package core.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.box.BoxOfUnits;
import core.object.Group;
import core.object.Unit;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueryGroupDataRequest extends RequestToServer {
    {
        handle = Handle.QUERY;
        port = 3008;
    }

    private transient int coalition;

    public QueryGroupDataRequest(Integer coalition) {
        this.coalition = coalition;
    }

    @Override
    public void resolve(String object) {
        Gson gson = new Gson();
//        Type targetType = new TypeToken<ArrayList<Group>>(){}.getType();
//        String json = gson.toJson(object);
//        ArrayList<Group> groups = gson.fromJson(json, targetType);
//        ConcurrentMap<String, Unit> updateMap = groups.stream()
//                .flatMap(g -> g.getUnits().stream())
//                .collect(Collectors.toConcurrentMap(Unit::getName, Function.identity()));
//
//        BoxOfUnits.putBox(updateMap);



//        System.out.println("updateMap = " + updateMap);
    }
}
