package moe.ofs.backend.services.mizdb;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.request.DataRequest;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.MissionKeyValueService;
import moe.ofs.backend.util.LuaScripts;

import java.lang.reflect.Type;
import java.util.*;

public abstract class AbstractPersistentKeyValueService<T> implements MissionKeyValueService<T> {
    protected RequestTransmissionService requestTransmissionService;

    public AbstractPersistentKeyValueService(RequestTransmissionService requestTransmissionService) {
        this.requestTransmissionService = requestTransmissionService;
    }

    private String query(String debugString) {
        Environment environment = this.getClass().getAnnotation(InjectionEnvironment.class).value();
        switch (environment) {
            case MISSION:
                return ((ServerDataRequest) requestTransmissionService
                        .send(new ServerDataRequest(debugString))).get();
            case HOOK:
                return ((ServerDataRequest) requestTransmissionService
                        .send(new ServerDataRequest(DataRequest.State.DEBUG, debugString))).get();
            case EXPORT:
                throw new RuntimeException("EXPORT NOT IMPLEMENTED");
            case TRIGGER:
                throw new RuntimeException("TRIGGER NOT IMPLEMENTED");
            default:
                throw new RuntimeException();
        }
    }

    private void execute(String debugString) {
        Environment environment = this.getClass().getAnnotation(InjectionEnvironment.class).value();
        switch (environment) {
            case MISSION:
                requestTransmissionService.send(new ServerDataRequest(debugString));
                break;
            case HOOK:
                requestTransmissionService.send(new ServerDataRequest(DataRequest.State.DEBUG, debugString));
                break;
            case EXPORT:
                throw new RuntimeException("EXPORT NOT IMPLEMENTED");
            case TRIGGER:
                throw new RuntimeException("TRIGGER NOT IMPLEMENTED");
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public Set<T> findAll(Class<T> tClass) {
        Gson gson = new Gson();

        String dataJson = query(LuaScripts.loadAndPrepare("storage/keyvalue/kw_pair_get_all.lua",
                        getRepositoryName()));

//        System.out.println("dataJson = " + dataJson);
        Type type = TypeToken.getParameterized(ArrayList.class, tClass).getType();

        ArrayList<T> list = gson.fromJson(dataJson, type);

        return new HashSet<>(list);
    }

    @Override
    public void deleteAll() {
        execute(LuaScripts.loadAndPrepare("storage/keyvalue/kw_pair_delete_all.lua",
                        getRepositoryName()));
    }

    @Override
    public T save(Object key, T object) {
        Gson gson = new Gson();
        String keyJson = gson.toJson(key);
        String objectJson = gson.toJson(object);

        System.out.println("keyJson = " + keyJson);

        execute(LuaScripts.loadAndPrepare("storage/keyvalue/kw_pair_save.lua",
                        getRepositoryName(), keyJson, objectJson));
        return object;
    }

    @Override
    public T save(Map.Entry<Object, T> entry) {
        save(entry.getKey(), entry.getValue());
        return entry.getValue();
    }

    @Override
    public void delete(Object key) {
        Gson gson = new Gson();
        String keyJson = gson.toJson(key);

        execute(LuaScripts.loadAndPrepare("storage/keyvalue/kw_pair_delete.lua",
                        getRepositoryName(), keyJson));
    }

    @Override
    public Optional<T> find(Object key, Class<T> tClass) {
        Gson gson = new Gson();
        String keyJson = gson.toJson(key);

        String dataJson = query(LuaScripts.loadAndPrepare("storage/keyvalue/kw_pair_get.lua",
                                getRepositoryName(), keyJson));

        return Optional.of(gson.fromJson(dataJson, tClass));
    }

    @Override
    public Optional<T> fetch(Object key, Class<T> tClass) {
        Gson gson = new Gson();
        String keyJson = gson.toJson(key);

        String dataJson = query(LuaScripts.loadAndPrepare("storage/keyvalue/kw_pair_fetch.lua",
                                getRepositoryName(), keyJson));

        return Optional.of(gson.fromJson(dataJson, tClass));
    }

    @Override
    public Set<T> fetchAll(Class<T> tClass) {
        Gson gson = new Gson();

        String dataJson = query(LuaScripts.loadAndPrepare("storage/keyvalue/kw_pair_fetch_all.lua",
                        getRepositoryName()));

        Type type = TypeToken.getParameterized(ArrayList.class, tClass).getType();

        ArrayList<T> list = gson.fromJson(dataJson, type);

        return new HashSet<>(list);
    }

    @Override
    public Set<T> fetchMapAll(String mapper, Class<T> tClass) {
        Gson gson = new Gson();

        String dataJson = query(LuaScripts.loadAndPrepare("storage/keyvalue/kw_pair_fetch_mapping_all.lua",
                        getRepositoryName(), mapper));

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
    public void resetRepository() {
        deleteAll();
    }

    @Override
    public void createRepository() {
        execute(LuaScripts.loadAndPrepare("storage/keyvalue/kw_pair_create.lua",
                        getRepositoryName()));
    }
}
