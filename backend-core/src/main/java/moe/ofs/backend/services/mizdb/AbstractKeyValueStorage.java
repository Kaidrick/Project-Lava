package moe.ofs.backend.services.mizdb;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.services.MissionKeyValueService;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
public abstract class AbstractKeyValueStorage<T> implements MissionKeyValueService<T> {
    protected String name;
    protected LuaQueryEnv env;

    @Override
    public String getRepositoryName() {
        return name;
    }

    public AbstractKeyValueStorage(String name, LuaQueryEnv env) {
        this.name = name;
        this.env = env;

        MissionStartObservable missionStartObservable = s -> {
            createRepository();
            log.info("Creating KeyValue Storage: {} in {}", name, env.getEnv());
        };
        missionStartObservable.register();
    }

    @Override
    public Set<T> findAll(Class<T> tClass) {
        return LuaScripts.requestWithFile(env, "storage/keyvalue/kw_pair_get_all.lua")
                .getAsSetFor(tClass);
    }

    @Override
    public void deleteAll() {
        LuaScripts.requestWithFile(env, "storage/keyvalue/kw_pair_delete_all.lua",
                getRepositoryName());
    }

    @Override
    public T save(Object key, T object) {
        Gson gson = new Gson();
        String keyJson = gson.toJson(key);
        String objectJson = gson.toJson(object);

        LuaScripts.requestWithFile(env, "storage/keyvalue/kw_pair_save.lua",
                getRepositoryName(), keyJson, objectJson);
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

        LuaScripts.requestWithFile(env, "storage/keyvalue/kw_pair_delete.lua",
                getRepositoryName(), keyJson);
    }

    @Override
    public Optional<T> find(Object key, Class<T> tClass) {
        Gson gson = new Gson();
        String keyJson = gson.toJson(key);

        return Optional.of(LuaScripts.requestWithFile(env,
                "storage/keyvalue/kw_pair_get.lua",
                getRepositoryName(), keyJson).getAs(tClass));
    }

    @Override
    public Optional<T> fetch(Object key, Class<T> tClass) {
        Gson gson = new Gson();
        String keyJson = gson.toJson(key);

        return Optional.of(LuaScripts.requestWithFile(env, "storage/keyvalue/kw_pair_fetch.lua",
                getRepositoryName(), keyJson).getAs(tClass));
    }

    @Override
    public Set<T> fetchAll(Class<T> tClass) {
        return LuaScripts.requestWithFile(env, "storage/keyvalue/kw_pair_fetch_all.lua",
                getRepositoryName()).getAsSetFor(tClass);
    }

    @Override
    public Set<T> fetchMapAll(String mapper, Class<T> tClass) {
        try {
            return LuaScripts.requestWithFile(env, "storage/keyvalue/kw_pair_fetch_mapping_all.lua",
                    getRepositoryName(), mapper).getAsSetFor(tClass);
        } catch (JsonSyntaxException e) {
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
        LuaScripts.requestWithFile(env, "storage/keyvalue/kw_pair_create.lua",
                getRepositoryName());
    }
}
