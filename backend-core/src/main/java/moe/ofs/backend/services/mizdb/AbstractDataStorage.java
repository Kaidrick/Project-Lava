package moe.ofs.backend.services.mizdb;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.function.mizdb.services.MissionDataService;
import moe.ofs.backend.connector.util.LuaScripts;
import moe.ofs.backend.connector.lua.LuaQueryEnv;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Slf4j
public abstract class AbstractDataStorage<T> implements MissionDataService<T> {
    protected String name;
    protected LuaQueryEnv env;

    protected AbstractDataStorage(String name, LuaQueryEnv env) {
        this.name = name;
        this.env = env;

        MissionDataService.super.createRepository();
    }

    @Override
    public Set<T> findAll(Class<T> tClass) {
        return LuaScripts.requestWithFile(env,
                "storage/common/table_find_all.lua").getAsSetFor(tClass);
    }

    @Override
    public Optional<T> findBy(String attributeName, Object value, Class<T> tClass) {
        T t = LuaScripts.requestWithFile(env, "storage/common/table_find_by_attribute_name.lua",
                getRepositoryName(), attributeName, value).getAs(tClass);

        return t != null ? Optional.of(t) : Optional.empty();
    }

    @Override
    public void deleteAll() {
        LuaScripts.requestWithFile(env, "storage/common/table_delete_all.lua",
                        getRepositoryName());
    }

    @Override
    public Optional<T> findById(Long id, Class<T> tClass) {
        return Optional.empty();
    }

    @Override
    public T save(T object) {
        Gson gson = new Gson();
        String objectJson = gson.toJson(object);

        LuaScripts.requestWithFile(env, "storage/common/table_save.lua",
                        getRepositoryName(), objectJson);
        return object;
    }

    @Override
    public void delete(T object) {
        Gson gson = new Gson();
        String objectJson = gson.toJson(object);

        LuaScripts.requestWithFile(env, "storage/common/table_delete.lua",
                        getRepositoryName(), objectJson);
    }

    @Override
    public void deleteBy(String attributeName, Object value) {
        LuaScripts.requestWithFile(env, "storage/common/table_find_by_attribute_name.lua",
                                getRepositoryName(), attributeName, value);
    }

    @Override
    public Set<T> fetchAll(Class<T> tClass) {
        return LuaScripts.requestWithFile(env, "storage/common/table_fetch_all.lua")
                .getAsSetFor(tClass);
    }

    @Override
    public Set<T> fetchMapAll(String mapper, Class<T> tClass) {
        try {
            return LuaScripts.requestWithFile(env, "storage/common/table_fetch_mapping_all.lua",
                    getRepositoryName(), mapper).getAsSetFor(tClass);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();

            return Collections.emptySet();
        }
    }

    @Override
    public Optional<T> fetchById(Long id, Class<T> tClass) {
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
    public boolean createRepository() {
        log.info("Creating Data Storage: {} in {}", name, env.getEnv());

        return LuaScripts.requestWithFile(env, "storage/common/table_create.lua",
                        getRepositoryName()).getAsBoolean();
    }
}
