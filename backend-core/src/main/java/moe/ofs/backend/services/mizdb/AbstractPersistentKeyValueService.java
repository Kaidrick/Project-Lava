package moe.ofs.backend.services.mizdb;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.function.mizdb.services.MissionKeyValueService;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.QueryOnAnnotation;

import java.lang.reflect.Type;
import java.util.*;

public abstract class AbstractPersistentKeyValueService<T> extends QueryOnAnnotation implements MissionKeyValueService<T> {

    @Override
    public Set<T> findAll(Class<T> tClass) {
        return query(LuaScripts.loadAndPrepare("storage/keyvalue/kw_pair_get_all.lua",
                getRepositoryName())).getAsSetFor(tClass);
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
                                getRepositoryName(), keyJson)).get();

        return Optional.of(gson.fromJson(dataJson, tClass));
    }

    @Override
    public Optional<T> fetch(Object key, Class<T> tClass) {
        Gson gson = new Gson();
        String keyJson = gson.toJson(key);

        String dataJson = query(LuaScripts.loadAndPrepare("storage/keyvalue/kw_pair_fetch.lua",
                                getRepositoryName(), keyJson)).get();

        return Optional.of(gson.fromJson(dataJson, tClass));
    }

    @Override
    public Set<T> fetchAll(Class<T> tClass) {
        Gson gson = new Gson();

        String dataJson = query(LuaScripts.loadAndPrepare("storage/keyvalue/kw_pair_fetch_all.lua",
                        getRepositoryName())).get();

        Type type = TypeToken.getParameterized(ArrayList.class, tClass).getType();

        ArrayList<T> list = gson.fromJson(dataJson, type);

        return new HashSet<>(list);
    }

    @Override
    public Set<T> fetchMapAll(String mapper, Class<T> tClass) {
        Gson gson = new Gson();

        String dataJson = query(LuaScripts.loadAndPrepare("storage/keyvalue/kw_pair_fetch_mapping_all.lua",
                        getRepositoryName(), mapper)).get();

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
    public boolean createRepository() {
        return query(LuaScripts.loadAndPrepare("storage/keyvalue/kw_pair_create.lua",
                        getRepositoryName())).getAsBoolean();
    }
}
