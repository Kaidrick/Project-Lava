package moe.ofs.backend.services;

import moe.ofs.backend.object.BaseEntity;

public interface ObjectRepositoryService<T extends BaseEntity> {

    T save(T object);

    T update(T object);

    void delete(T object);

}
