package moe.ofs.backend.services;

import moe.ofs.backend.object.ExportObject;
import moe.ofs.backend.repositories.ExportObjectHashMapRepository;
import moe.ofs.backend.repositories.ExportObjectRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ExportObjectJpaService implements ExportObjectService {

    private final ExportObjectRepository exportObjectRepository;
    public ExportObjectJpaService(ExportObjectRepository exportObjectRepository) {
        this.exportObjectRepository = exportObjectRepository;
    }

    public void dispose() {
        exportObjectRepository.deleteAll();
    }

    @Override
    public ExportObject save(ExportObject object) {
        return exportObjectRepository.save(object);
    }

    @Override
    public ExportObject update(ExportObject update) {
        // TODO --> performance?

        return null;
    }

    @Override
    public void delete(ExportObject object) {
        exportObjectRepository.delete(object);
    }

    @Override
    public Map<Long, ExportObject> findAll() {
        return StreamSupport.stream(exportObjectRepository.findAll().spliterator(), true)
                .collect(Collectors.toMap(ExportObject::getRuntimeID, Function.identity()));
    }

    @Override
    public Optional<ExportObject> findByRuntimeId(long runtimeId) {
        return exportObjectRepository.findById(runtimeId);
    }

    @Override
    public Optional<ExportObject> findByUnitName(String unitName) {
        return exportObjectRepository.findByUnitName(unitName);
    }

    @Override
    public void deleteByRuntimeId(long runtimeId) {
        exportObjectRepository.deleteByRuntimeID(runtimeId);
    }
}
