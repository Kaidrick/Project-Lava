package moe.ofs.backend.services;

import moe.ofs.backend.domain.Level;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface MissionKeyValueService<T> extends MissionPersistenceService {
    Set<T> findAll(Class<T> tClass);

    void deleteAll();

    Optional<T> find(Object key, Class<T> tClass);

    T save(Object key, T object);

    T save(Map.Entry<Object, T> entry);

    void delete(Object key);

    Set<T> fetchAll(Class<T> tClass);

    Set<T> fetchMapAll(String mapper, Class<T> tClass);

    Optional<T> fetch(Object key, Class<T> tClass);
}
