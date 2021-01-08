package moe.ofs.backend.services.mizdb;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.function.mizdb.services.MissionDataService;
import moe.ofs.backend.connector.util.LuaScripts;
import moe.ofs.backend.connector.lua.QueryOnAnnotation;

import java.lang.reflect.Type;
import java.util.*;

public abstract class AbstractPersistentMissionDataService<T> extends QueryOnAnnotation implements MissionDataService<T> {

    @Override
    public Set<T> findAll(Class<T> tClass) {
        return query(LuaScripts.loadAndPrepare("storage/common/table_find_all.lua",
                        getRepositoryName())).getAsSetFor(tClass);
    }

    @Override
    public Optional<T> findBy(String attributeName, Object value, Class<T> tClass) {
        Gson gson = new Gson();

        String dataJson = query(
                        LuaScripts.loadAndPrepare("storage/common/table_find_by_attribute_name.lua",
                                getRepositoryName(), attributeName, value)).get();

        T t = gson.fromJson(dataJson, tClass);

        return t != null ? Optional.of(t) : Optional.empty();
    }

    @Override
    public void deleteAll() {
        execute(LuaScripts.loadAndPrepare("storage/common/table_delete_all.lua",
                        getRepositoryName()));
    }

    @Override
    public Optional<T> findById(Long id, Class<T> tClass) {
        return Optional.empty();
    }

    @Override
    public T save(T object) {
        Gson gson = new Gson();
        String objectJson = gson.toJson(object);

        execute(LuaScripts.loadAndPrepare("storage/common/table_save.lua",
                getRepositoryName(), objectJson));

        return object;
    }

    @Override
    public void delete(T object) {
        Gson gson = new Gson();
        String objectJson = gson.toJson(object);

        execute(LuaScripts.loadAndPrepare("storage/common/table_delete.lua",
                getRepositoryName(), objectJson));
    }

    @Override
    public void deleteBy(String attributeName, Object value) {
        execute(LuaScripts.loadAndPrepare("storage/common/table_find_by_attribute_name.lua",
                getRepositoryName(), attributeName, value));
    }

    @Override
    public Set<T> fetchAll(Class<T> tClass) {
        Gson gson = new Gson();

        String dataJson = query(LuaScripts.loadAndPrepare("storage/common/table_fetch_all.lua",
                        getRepositoryName())).get();

//        System.out.println("dataJson = " + dataJson);
        Type type = TypeToken.getParameterized(ArrayList.class, tClass).getType();

        ArrayList<T> list = gson.fromJson(dataJson, type);

        return new HashSet<>(list);
    }

    @Override
    public Set<T> fetchMapAll(String mapper, Class<T> tClass) {
        Gson gson = new Gson();

        String dataJson = query(LuaScripts.loadAndPrepare("storage/common/table_fetch_mapping_all.lua",
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
        return query(LuaScripts.loadAndPrepare("storage/common/table_create.lua", getRepositoryName()))
                .getAsBoolean();
    }
}
