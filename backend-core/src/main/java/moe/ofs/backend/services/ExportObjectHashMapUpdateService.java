package moe.ofs.backend.services;

import com.google.common.collect.Sets;
import moe.ofs.backend.object.ExportObject;
import moe.ofs.backend.repositories.ExportObjectHashMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class ExportObjectHashMapUpdateService implements ExportObjectService {

    private final ExportObjectHashMapRepository exportObjectHashMapRepository;

    @Autowired
    public ExportObjectHashMapUpdateService(ExportObjectHashMapRepository exportObjectHashMapRepository) {

        this.exportObjectHashMapRepository = exportObjectHashMapRepository;

    }

    @Override
    public ExportObject save(ExportObject object) {
        if(object != null) {
            return exportObjectHashMapRepository.save(object.getRuntimeID(), object);
        } else {
            return null;
        }
    }

    /**
     * Update a single ExportObject in the HashMap
     * @param updatedObject new object to be used as reference
     * @return the updated ExportObject
     */
    @Override
    public ExportObject update(ExportObject updatedObject) {
        // find object
        ExportObject objectRecord = exportObjectHashMapRepository.findByRuntimeId(updatedObject.getRuntimeID())
                .orElseThrow(() -> new RuntimeException("Unable to find ExportObject with RuntimeID: " +
                        updatedObject.getRuntimeID()));


        if(runtimeDataChanged(updatedObject, objectRecord)) {
            // update record with new data
            objectRecord.setFlags(updatedObject.getFlags());
            objectRecord.setLatLongAlt(updatedObject.getLatLongAlt());
            objectRecord.setPosition(updatedObject.getPosition());
            objectRecord.setHeading(updatedObject.getHeading());
            objectRecord.setBank(updatedObject.getBank());
            objectRecord.setPitch(updatedObject.getPitch());

            exportObjectHashMapRepository.save(objectRecord.getRuntimeID(), objectRecord);
        }

        // return updated object
        return objectRecord;
    }

    @Override
    public Optional<ExportObject> findByRuntimeId(long runtimeId) {
        return exportObjectHashMapRepository.findByRuntimeId(runtimeId);
    }

    @Override
    public Optional<ExportObject> findByUnitName(String unitName) {
        return exportObjectHashMapRepository.findByUnitName(unitName);
    }

    @Override
    public void delete(ExportObject object) {
        exportObjectHashMapRepository.delete(object);
    }

    @Override
    public Map<Long, ExportObject> findAll() {
        return exportObjectHashMapRepository.findAll();
    }

    @Override
    public void deleteByRuntimeId(long runtimeId) {
        exportObjectHashMapRepository.deleteById(runtimeId);
    }


    // compare with previous map to determine
    public void addOrUpdateAll(List<ExportObject> exportObjectList) {
        // update position and attitude for existing export object
        Predicate<ExportObject> exist = exportObjectHashMapRepository::exist;
        Predicate<ExportObject> notExist = exist.negate();

        exportObjectList.stream().filter(notExist).forEach(objectUpdate -> {
            // create new entry
            exportObjectHashMapRepository.save(objectUpdate.getRuntimeID(), objectUpdate);
        });

        exportObjectList.stream().filter(exist).forEach(this::update);
    }

    /**
     * If position or bank angle or pitch or heading changed, it means the object data need to be updated
     * @param update new data parse from json
     * @param record previous data in the HashMap
     * @return boolean value indicating whether an update is needed
     */
    private boolean runtimeDataChanged(ExportObject update, ExportObject record) {
        return !record.getPosition().equals(update.getPosition()) ||
                record.getBank() != update.getBank() ||
                record.getHeading() != update.getHeading() ||
                record.getPitch() != update.getPitch() ||
               !record.getFlags().equals(update.getFlags());
    }

    public void dispose() {
        exportObjectHashMapRepository.dispose();
    }
}
