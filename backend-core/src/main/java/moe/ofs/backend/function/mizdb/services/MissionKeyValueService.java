package moe.ofs.backend.function.mizdb.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a key-value pair that can be stored in Lua table.
 * @param <T> Generic type of the value object to be stored as the value.
 */
public interface MissionKeyValueService<T> extends MissionPersistenceService {

    /**
     * Retrieve all values from the storage and convert it to a set for type T.
     * @param tClass Class object of the generic type T.
     * @return Set for generic type T.
     */
    Set<T> findAll(Class<T> tClass);

    /**
     * Remove all data from the storage.lua
     */
    void deleteAll();

    /**
     * Search in the storage and return the value matching the specified key, if existed.
     * @param key Key value of the key-value pair to be searched for.
     * @param tClass Class object type of which the return JSON String will be converted to.
     * @return Optional of type T.
     */
    Optional<T> find(Object key, Class<T> tClass);

    /**
     * Save a key-value pair to the storage and return saved value.
     * @param key the key value of the pair.
     * @param object the object value of the pair.
     * @return the value saved to Lua storage
     */
    T save(Object key, T object);

    T save(Map.Entry<Object, T> entry);

    /**
     * Save a map to Lua storage, where the entries of the map are treated as key-value pairs.
     * @param map The map whose entries are to be saved into the storage.
     * @return List for generic type T which represents the values saved to the storage.
     */
    List<T> saveAll(Map<Object, T> map);

    /**
     * Delete one kay value pair from the storage by key.
     * @param key the key object to be used to search in the storage for delete operation.
     */
    void delete(Object key);

    /**
     * Fetch all pairs from the storage; fetch will remove data from the original storage.
     * @param tClass Class object type of which the return JSON String will be converted to.
     * @return Set for generic type T.
     */
    Set<T> fetchAll(Class<T> tClass);

    /**
     * Fetch all pairs from the storage; fetch will remove data from the original storage; the specified mapper
     * will be used on each data item before they are sent to a list for return as JSON String.
     * @param mapper a Lua string to be loaded as the mapping function that takes the original data and
     *               maps it to another value or table.
     * @param tClass Class object type of which the return JSON String will be converted to.
     * @return Set for generic type T.
     */
    Set<T> fetchMapAll(String mapper, Class<T> tClass);

    /**
     * Fetch one value from the storage by key; fetch will remove the data from original storage.
     * @param key the key object to be used to fetch in the storage for fetch operation.
     * @param tClass Class object type of which the return JSON String will be converted to.
     * @return Optional of type T.
     */
    Optional<T> fetch(Object key, Class<T> tClass);

    /**
     * Precache is for scenarios where the implementation object is created before connection can be established
     * between Lava backend and DCS World Lua server. In this case, the implemented method should handle
     * a cache mechanism that allows the data to be saved to a temporary list and sent to Lua storage when proper
     * connection has been made and dependencies injected.
     */
    void precache();
}
