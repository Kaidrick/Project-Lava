package moe.ofs.backend.repositories;

import moe.ofs.backend.domain.ExportObject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExportObjectRepository // extends JpaRepository<ExportObject, Long>
{

    Optional<ExportObject> findByUnitName(String name);

    Optional<ExportObject> findByRuntimeID(Long runtimeId);

    void deleteByRuntimeID(Long runtimeId);
}
