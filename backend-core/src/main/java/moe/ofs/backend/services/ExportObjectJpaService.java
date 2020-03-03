package moe.ofs.backend.services;

import moe.ofs.backend.object.ExportObject;
import moe.ofs.backend.repositories.ExportObjectRepository;
import org.springframework.stereotype.Service;

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
    public Optional<ExportObject> findByRuntimeId(int runtimeId) {
        return Optional.empty();
    }
}
