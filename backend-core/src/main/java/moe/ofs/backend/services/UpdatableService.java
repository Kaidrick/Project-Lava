package moe.ofs.backend.services;

import moe.ofs.backend.domain.BaseEntity;
import org.apache.http.annotation.Obsolete;

import java.util.List;

public interface UpdatableService<T extends BaseEntity> {

    T update(T updateObject);

    void add(T newObject);

    void remove(T obsoleteObject);

    @Obsolete
    void cycle(List<T> list);

    boolean updatable(T update, T record);
}
