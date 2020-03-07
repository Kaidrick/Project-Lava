package moe.ofs.backend.services;

import moe.ofs.backend.domain.BaseEntity;

import java.util.List;

public interface UpdatableService<T extends BaseEntity> {

    void update(T updateObject);

    void add(T newObject);

    void remove(T obsoleteObject);

    void cycle(List<T> list);

    boolean updatable(T update, T record);
}
