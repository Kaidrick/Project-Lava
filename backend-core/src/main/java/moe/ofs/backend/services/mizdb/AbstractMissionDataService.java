package moe.ofs.backend.services.mizdb;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.services.MissionDataService;
import moe.ofs.backend.util.LuaScripts;

import java.lang.reflect.Type;
import java.util.*;

public abstract class AbstractMissionDataService<T> implements MissionDataService<T> {

    @Override
    public Set<T> findAll(Class<T> tClass) {
        Gson gson = new Gson();
        String dataJson = new ServerDataRequest(LuaScripts.loadAndPrepare("mizdb/table_find_all.lua",
                getRepositoryName())).get();

        System.out.println("find all");

        System.out.println("dataJson = " + dataJson);
        Type type = TypeToken.getParameterized(ArrayList.class, tClass).getType();

        ArrayList<T> list = gson.fromJson(dataJson, type);

        return new HashSet<>(list);
    }

    @Override
    public Optional<T> findBy(String attributeName, Object value, Class<T> tClass) {
        Gson gson = new Gson();
        String dataJson = new ServerDataRequest(
                LuaScripts.loadAndPrepare("mizdb/table_find_by_attribute_name.lua",
                getRepositoryName(), attributeName, value)).get();

        System.out.println("find by");

        T t = gson.fromJson(dataJson, tClass);

        return t != null ? Optional.of(t) : Optional.empty();
    }

    @Override
    public void deleteAll() {
        new ServerExecRequest(LuaScripts.loadAndPrepare("mizdb/table_delete_all.lua",
                getRepositoryName())).send();
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public T save(T object) {
        Gson gson = new Gson();
        String objectJson = gson.toJson(object);

        new ServerExecRequest(LuaScripts.loadAndPrepare("mizdb/table_save.lua",
                        getRepositoryName(), objectJson)).send();
        return object;
    }

    @Override
    public void delete(T object) {
        Gson gson = new Gson();
        String objectJson = gson.toJson(object);

        new ServerExecRequest(LuaScripts.loadAndPrepare("mizdb/table_delete.lua",
                getRepositoryName(), objectJson)).send();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void resetRepository() {
        deleteAll();
    }

    @Override
    public void createRepository() {
        new ServerExecRequest(LuaScripts.loadAndPrepare("mizdb/create_table.lua",
                        getRepositoryName())).send();
    }
}
