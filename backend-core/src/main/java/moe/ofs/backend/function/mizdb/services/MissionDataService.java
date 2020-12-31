package moe.ofs.backend.function.mizdb.services;

import java.util.Optional;
import java.util.Set;

/**
 * Represents a list of data that can be stored in Lua table.
 * @param <T> Generic type of the value object to be stored as the value.
 */
public interface MissionDataService<T> extends MissionPersistenceService {

    /**
     * Retrieve all values from the storage and convert it to a set for type T.
     * @param tClass Class object of the generic type T.
     * @return Set for generic type T.
     */
    Set<T> findAll(Class<T> tClass);

    /**
     * Retrieve one object from the storage by attribute name and comparing the attribute value to the given value.
     * @param attributeName the attribute name to be used in the said comparison.
     * @param value the attribute to which the attribute value equals will be retrieved.
     * @param tClass Class object type of which the return JSON String will be converted to.
     * @return Optional of generic T which represents an optional of the searched object.
     */
    Optional<T> findBy(String attributeName, Object value, Class<T> tClass);

    /**
     * Remove all data from the storage.lua
     */
    void deleteAll();

    /**
     * Retrieve one object from the storage by id.
     * @param id the id of the object to be retrieved.
     * @param tClass Class object type of which the return JSON String will be converted to.
     * @return Optional of generic T which represents an optional of the searched object.
     */
    Optional<T> findById(Long id, Class<T> tClass);

    /**
     * Save an object to Lua storage.
     * @param object the object to be saved.
     * @return The object to be saved.
     */
    T save(T object);

    /**
     * Delete an object from Lua storage.
     * @param object the object to be deleted.
     */
    void delete(T object);

    /**
     * Delete an object from Lua storage by id.
     * @param id the id of the object to be deleted.
     */
    void deleteById(Long id);

    /**
     * Delete one object from the storage by attribute name and comparing the attribute value to the given value.
     * @param attributeName the attribute name to be used in the said comparison.
     * @param value the attribute to which the attribute value equals will be retrieved.
     */
    void deleteBy(String attributeName, Object value);

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
     * Fetch one object from the storage by id; fetch will remove the data from original storage.
     * @param id the id to be used to fetch in the storage for fetch operation.
     * @param tClass Class object type of which the return JSON String will be converted to.
     * @return Optional of generic T which represents an optional of the searched object.
     */
    Optional<T> fetchById(Long id, Class<T> tClass);

    /**
     * Fetch one object from the storage by attribute name and comparing the attribute value to the given value.
     * @param attributeName the attribute name to be used in the said comparison.
     * @param value the attribute to which the attribute value equals will be retrieved.
     * @param tClass Class object type of which the return JSON String will be converted to.
     * @return Optional of generic T which represents an optional of the searched object.
     */
    Optional<T> fetchBy(String attributeName, Object value, Class<T> tClass);
}
