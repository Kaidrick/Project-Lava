package moe.ofs.backend.services.mizdb;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.BackgroundTask;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.message.OperationPhase;
import moe.ofs.backend.services.MissionKeyValueService;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;

import java.util.*;

@Slf4j
public abstract class AbstractKeyValueStorage<T> implements MissionKeyValueService<T> {
    protected String name;
    protected LuaQueryEnv env;

    private final Map<Object, T> precachedValues = new HashMap<>();

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

    /**
     * If operation phase is not running, add to cache; otherwise run normally
     * @param key Object key value of the pair
     * @param object T to be save as the value of the pair
     * @return object saved to key value pairs
     */
    @Override
    public T save(Object key, T object) {
        if (BackgroundTask.getCurrentTask() != null &&
            BackgroundTask.getCurrentTask().getPhase() == OperationPhase.RUNNING) {
            Gson gson = new Gson();
            String keyJson = gson.toJson(key);
            String objectJson = gson.toJson(object);

            LuaScripts.requestWithFile(env, "storage/keyvalue/kw_pair_save.lua",
                    getRepositoryName(), keyJson, objectJson);
        } else {  // add to cache
            log.info("{} precache with key: {}, value: {}", getClass().getName(), key, object);
            precachedValues.put(key, object);
        }
        return object;
    }

    /**
     * If operation phase is not running, add to cache; otherwise run normally
     * @param map
     * @return
     */
    @Override
    public List<T> saveAll(Map<Object, T> map) {
        if (BackgroundTask.getCurrentTask().getPhase() == OperationPhase.RUNNING) {
            Gson gson = new Gson();
            LuaScripts.requestWithFile(env, "storage/keyvalue/kw_pair_save_batch.lua",
                    getRepositoryName(), gson.toJson(map));
        } else {
            precachedValues.putAll(map);
        }
        return new ArrayList<>(map.values());
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
    public void precache() {
        saveAll(precachedValues);
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

        precache();  // always run precache when creating a new repository
    }
}
