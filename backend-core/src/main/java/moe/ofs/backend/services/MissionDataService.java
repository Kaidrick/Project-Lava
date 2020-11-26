package moe.ofs.backend.services;

import java.util.Optional;
import java.util.Set;

public interface MissionDataService<T> extends MissionPersistenceService {
    Set<T> findAll(Class<T> tClass);

    Optional<T> findBy(String attributeName, Object value, Class<T> tClass);

    void deleteAll();

    Optional<T> findById(Long id);

    T save(T object);

    void delete(T object);

    void deleteById(Long id);

    void deleteBy(String attributeName, Object value);

    Set<T> fetchAll(Class<T> tClass);

    Optional<T> fetchById(Long id);

    Optional<T> fetchBy(String attributeName, Object value, Class<T> tClass);
}
