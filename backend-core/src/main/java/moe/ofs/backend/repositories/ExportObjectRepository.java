package moe.ofs.backend.repositories;

import moe.ofs.backend.object.ExportObject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ExportObjectRepository extends CrudRepository<ExportObject, Long> {

    Optional<ExportObject> findById(Long id);

    Optional<ExportObject> findByUnitName(String name);

    void deleteByRuntimeID(Long runtimeId);
}
