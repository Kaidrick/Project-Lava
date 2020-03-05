package moe.ofs.backend.services;

import moe.ofs.backend.object.ExportObject;
import moe.ofs.backend.repositories.ExportObjectRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class ExportObjectJpaService implements ExportObjectService {

    private final ExportObjectRepository exportObjectRepository;

    public ExportObjectJpaService(ExportObjectRepository exportObjectRepository) {
        this.exportObjectRepository = exportObjectRepository;
    }

    @Override
    public ExportObject save(ExportObject object) {
        return exportObjectRepository.save(object);
    }

    @Override
    public ExportObject update(ExportObject object) {
        // compare value
        return null;
    }

    @Override
    public void delete(ExportObject object) {

    }

    @Override
    public Map<Long, ExportObject> findAll() {
        return null;
    }

    @Override
    public Optional<ExportObject> findByRuntimeId(long runtimeId) {
        return Optional.empty();
    }

    @Override
    public Optional<ExportObject> findByUnitName(String unitName) {
        return Optional.empty();
    }

    @Override
    public void deleteByRuntimeId(long runtimeId) {

    }
}
