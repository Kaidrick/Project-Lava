package moe.ofs.backend.services;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.repositories.ExportObjectRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BoxOfExportUnitServiceImpl implements BoxOfExportUnitService {

    private final ExportObjectRepository exportObjectRepository;

    public BoxOfExportUnitServiceImpl(ExportObjectRepository exportObjectRepository) {
        this.exportObjectRepository = exportObjectRepository;
    }

    @Override
    public List<ExportObject> getAll() {
        return new ArrayList<>(exportObjectRepository.findAll());
    }
}
