package moe.ofs.backend.repositories;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractHashMapRepository<T, ID> implements HashMapRepository<T, ID> {

    HashMap<ID, T> map = new HashMap<>();

    @Override
    public void dispose() {
        map.clear();
    }

    @Override
    public Map<ID, T> findAll() {
        return new HashMap<>(map);
    }

    @Override
    public T save(ID id, T t) {
        return map.put(id, t);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public void delete(T t) {
        map.entrySet().stream()
                .filter(entry -> entry.getValue().equals(t))
                .findFirst().ifPresent(entry -> map.remove(entry.getKey()));
    }

    @Override
    public void deleteById(ID id) {
        map.remove(id);
    }

    @Override
    public boolean existById(ID id) {
        return map.get(id) != null;
    }

    @Override
    public boolean exist(T t) {
        return map.values().stream().anyMatch(object -> object.equals(t));
    }
}
