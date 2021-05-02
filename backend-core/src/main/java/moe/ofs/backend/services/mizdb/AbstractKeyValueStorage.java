package moe.ofs.backend.services.mizdb;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.connector.LavaSystemStatus;
import moe.ofs.backend.connector.lua.LuaQueryEnv;
import moe.ofs.backend.connector.util.LuaScripts;
import moe.ofs.backend.domain.connector.OperationPhase;
import moe.ofs.backend.domain.connector.handlers.scripts.ScriptInjectionTask;
import moe.ofs.backend.function.mizdb.services.MissionKeyValueService;
import moe.ofs.backend.function.mizdb.services.impl.LuaStorageInitServiceImpl;

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

    protected AbstractKeyValueStorage(String name, LuaQueryEnv env) {
        this.name = name;
        this.env = env;

        MissionKeyValueService.super.createRepository();

        // check whether task for LuaStorageInitService has been completed, if so, call createRepository()
        if (LavaSystemStatus.isInitiated()) {
            // TODO: why null check?
            Map<ScriptInjectionTask, Boolean> checkMap = LavaSystemStatus.getInjectionTaskChecks();
            Optional<ScriptInjectionTask> taskOptional = checkMap.keySet().stream()
                    .filter(task -> LuaStorageInitServiceImpl.class.equals(task.getInitializrClass()))
                    .findAny();

            boolean b = taskOptional.isPresent() && checkMap.get(taskOptional.get());

            if (b) {
                createRepository();
            }
        }

//        // FIXME: won't work for new kv store after initialization
//        // TODO: if started, do createRepository(), otherwise postpone
//        MissionStartObservable missionStartObservable = s -> {
//            createRepository();
//            log.info("Creating KeyValue Storage: {} in {}", name, env.getEnv());
//        };
//        missionStartObservable.register();
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
        if (LavaSystemStatus.getPhase() == OperationPhase.RUNNING) {
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
        if (LavaSystemStatus.getPhase() == OperationPhase.RUNNING ||
            LavaSystemStatus.getPhase() == OperationPhase.LOADING) {
            Gson gson = new Gson();
            LuaScripts.requestWithFile(env, "storage/keyvalue/kw_pair_save_batch.lua",
                    getRepositoryName(), gson.toJson(map));
        } else {
            log.info("{} precache with map: {}", getClass().getName(), map);
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
    public boolean createRepository() {
        log.info("Creating KeyValue Storage: {} in {}", name, env.getEnv());

        boolean created = LuaScripts.requestWithFile(env, "storage/keyvalue/kw_pair_create.lua",
                getRepositoryName()).getAsBoolean();

        precache();  // always run precache when creating a new repository

        return created;
    }
}
