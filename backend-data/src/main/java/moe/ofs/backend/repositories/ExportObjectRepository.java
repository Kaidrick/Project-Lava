package moe.ofs.backend.repositories;

import moe.ofs.backend.domain.ExportObject;

import java.util.Optional;
import java.util.Set;

public interface ExportObjectRepository // extends JpaRepository<ExportObject, Long>
{

    Optional<ExportObject> findByUnitName(String name);

    Optional<ExportObject> findByRuntimeID(Long runtimeId);

    void deleteByRuntimeID(Long runtimeId);

    Set<ExportObject> findAll();
}
