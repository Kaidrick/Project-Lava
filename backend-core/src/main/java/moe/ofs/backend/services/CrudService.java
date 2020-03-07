package moe.ofs.backend.services;

import java.util.Optional;
import java.util.Set;

public interface CrudService<T> {

    Set<T> findAll();

    void deleteAll();

    Optional<T> findById(Long id);

    T save(T object);

    void delete(T object);

    void deleteById(Long id);
}