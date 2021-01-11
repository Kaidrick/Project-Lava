package moe.ofs.backend.common;

import moe.ofs.backend.domain.dcs.poll.ExportObject;

import java.util.Optional;

public interface ExportObjectRepository // extends JpaRepository<ExportObject, Long>
{

    Optional<ExportObject> findByUnitName(String name);

    Optional<ExportObject> findByRuntimeID(Long runtimeId);

    void deleteByRuntimeID(Long runtimeId);
}