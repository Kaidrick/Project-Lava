package moe.ofs.backend.repositories;

import java.util.Map;
import java.util.Optional;

public interface HashMapRepository<T, ID> {
    T save(ID id, T t);

    Optional<T> findById(ID id);

    void delete(T t);

    void deleteById(ID id);

    boolean existById(ID id);

    boolean exist(T t);

    Map<ID, T> findAll();

    void dispose();
}
