package moe.ofs.backend.services.map;

import moe.ofs.backend.domain.BaseEntity;
import moe.ofs.backend.services.CrudService;

import java.util.*;

public abstract class AbstractMapService <T extends BaseEntity> implements CrudService<T> {

    protected Map<Long, T> map = new HashMap<>();

    protected Long getNextId() {
        return map.keySet().isEmpty() ? 1L : Collections.max(map.keySet()) + 1;
    }

    @Override
    public void deleteById(Long id) {
        map.remove(id);
    }

    @Override
    public T save(T object) {
        if (object == null) {
            throw new NullPointerException("Unable to save null value into MapService");
        } else if (object.getId() == null) {
            object.setId(getNextId());
        }

        map.put(object.getId(), object);
        return object;
    }

    @Override
    public void deleteAll() {
        map.clear();
    }

    @Override
    public Set<T> findAll() {
        return new HashSet<>(map.values());
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    public void delete(T t) {
        map.entrySet().removeIf(entry -> entry.getValue().equals(t));
    }

}
