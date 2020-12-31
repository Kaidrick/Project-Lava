package moe.ofs.backend.connector.services;

import moe.ofs.backend.dao.DcsExportObjectDao;
import moe.ofs.backend.domain.DcsExportObject;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class ExportObjectDataServiceImpl implements ExportObjectDataService {

    private final DcsExportObjectDao dcsExportObjectDao;

    public ExportObjectDataServiceImpl(DcsExportObjectDao dcsExportObjectDao) {
        this.dcsExportObjectDao = dcsExportObjectDao;
    }

    @Override
    public void add(DcsExportObject dcsExportObject) {

    }

    @Override
    public void remove(DcsExportObject dcsExportObject) {

    }

    @Override
    public void update(DcsExportObject dcsExportObject) {

    }

    @Override
    public boolean compare(DcsExportObject t1, DcsExportObject t2) {
        return false;
    }

    // TODO: maybe split crud operation to a

    @Override
    public Set<DcsExportObject> findAll() {
        return null;
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Optional<DcsExportObject> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public DcsExportObject save(DcsExportObject object) {
        return null;
    }

    @Override
    public void delete(DcsExportObject object) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
