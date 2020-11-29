package moe.ofs.backend.services;

import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.domain.GraveyardRecord;

import java.util.Optional;

/**
 * Graveyard is where obsolete ExportObject moves when it is no longer active in the sim environment.
 * The service can be used to search for killed unit.
 *
 * The runtime id is unique in DCS runtime by may be reused by other unit; therefore, a unique id
 * is needed for each object in the service.
 */
public interface GraveyardService {

    /**
     * Add obsolete ExportObject object to graveyard
     * @param record ExportObject deemed to be obsolete and no longer in use
     */
    void collect(ExportObject record);

    /**
     * Remove expired entries periodically from the graveyard for garbage collection.
     */
    void dispose();

    Optional<GraveyardRecord> findByRecordRuntimeId(long runtimeId);

}
