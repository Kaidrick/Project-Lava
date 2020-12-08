package moe.ofs.backend.services.mizdb;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.MissionDataService;
import moe.ofs.backend.util.LuaScripts;

import java.lang.reflect.Type;
import java.util.*;

public abstract class AbstractMissionDataService<T> implements MissionDataService<T> {
    protected RequestTransmissionService requestTransmissionService;

    public AbstractMissionDataService(RequestTransmissionService requestTransmissionService) {
        this.requestTransmissionService = requestTransmissionService;
    }

    @Override
    public Set<T> findAll(Class<T> tClass) {
        Gson gson = new Gson();

        String dataJson = ((ServerDataRequest) requestTransmissionService
                .send(new ServerDataRequest(LuaScripts.loadAndPrepare("storage/common/table_find_all.lua",
                        getRepositoryName())))).get();

//        System.out.println("dataJson = " + dataJson);
        Type type = TypeToken.getParameterized(ArrayList.class, tClass).getType();

        ArrayList<T> list = gson.fromJson(dataJson, type);

        return new HashSet<>(list);
    }

    @Override
    public Optional<T> findBy(String attributeName, Object value, Class<T> tClass) {
        Gson gson = new Gson();

        String dataJson = ((ServerDataRequest) requestTransmissionService
                .send(new ServerDataRequest(
                        LuaScripts.loadAndPrepare("storage/common/table_find_by_attribute_name.lua",
                                getRepositoryName(), attributeName, value)))).get();

        T t = gson.fromJson(dataJson, tClass);

        return t != null ? Optional.of(t) : Optional.empty();
    }

    @Override
    public void deleteAll() {
        requestTransmissionService.send(
                new ServerExecRequest(LuaScripts.loadAndPrepare("storage/common/table_delete_all.lua",
                        getRepositoryName()))
        );
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public T save(T object) {
        Gson gson = new Gson();
        String objectJson = gson.toJson(object);

        requestTransmissionService.send(
                new ServerExecRequest(LuaScripts.loadAndPrepare("storage/common/table_save.lua",
                        getRepositoryName(), objectJson))
        );
        return object;
    }

    @Override
    public void delete(T object) {
        Gson gson = new Gson();
        String objectJson = gson.toJson(object);

        requestTransmissionService.send(
                new ServerExecRequest(LuaScripts.loadAndPrepare("storage/common/table_delete.lua",
                        getRepositoryName(), objectJson))
        );
    }

    @Override
    public void deleteBy(String attributeName, Object value) {
        requestTransmissionService.send(
                new ServerExecRequest(
                        LuaScripts.loadAndPrepare("storage/common/table_find_by_attribute_name.lua",
                                getRepositoryName(), attributeName, value))
        );
    }

    @Override
    public Set<T> fetchAll(Class<T> tClass) {
        Gson gson = new Gson();

        String dataJson = ((ServerDataRequest) requestTransmissionService
                .send(new ServerDataRequest(LuaScripts.loadAndPrepare("storage/common/table_fetch_all.lua",
                        getRepositoryName())))).get();

//        System.out.println("dataJson = " + dataJson);
        Type type = TypeToken.getParameterized(ArrayList.class, tClass).getType();

        ArrayList<T> list = gson.fromJson(dataJson, type);

        return new HashSet<>(list);
    }

    @Override
    public Set<T> fetchMapAll(String mapper, Class<T> tClass) {
        Gson gson = new Gson();

        String dataJson = ((ServerDataRequest) requestTransmissionService
                .send(new ServerDataRequest(LuaScripts.loadAndPrepare("storage/common/table_fetch_mapping_all.lua",
                        getRepositoryName(), mapper)))).get();

        Type type = TypeToken.getParameterized(ArrayList.class, tClass).getType();

        ArrayList<T> list;
        try {
            list = gson.fromJson(dataJson, type);
            return new HashSet<>(list);
        } catch (JsonSyntaxException e) {
            System.out.println("dataJson = " + dataJson);
            e.printStackTrace();

            return Collections.emptySet();
        }
    }

    @Override
    public Optional<T> fetchById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<T> fetchBy(String attributeName, Object value, Class<T> tClass) {
        return Optional.empty();
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
        requestTransmissionService.send(
                new ServerExecRequest(LuaScripts.loadAndPrepare("storage/common/table_create.lua",
                        getRepositoryName()))
        );
    }
}
