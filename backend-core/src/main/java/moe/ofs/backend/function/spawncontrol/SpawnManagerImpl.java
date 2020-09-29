package moe.ofs.backend.function.spawncontrol;

import moe.ofs.backend.repositories.ExportObjectRepository;
import org.springframework.stereotype.Component;

@Component
public class SpawnManagerImpl implements SpawnManager {

    private final ExportObjectRepository exportObjectRepository;

    public SpawnManagerImpl(ExportObjectRepository exportObjectRepository) {
        this.exportObjectRepository = exportObjectRepository;
    }
}
