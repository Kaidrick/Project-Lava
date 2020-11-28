package moe.ofs.backend.domain;

public enum AssociateStrategy {
    MANDATORY,  // Every SimEvent must be associate with an ExportObject in map service or in graveyard service
    TRY_WITH_LIMIT,   // Put a limitation on number of retries made to search for an associated ExportObject
    MUTUAL_REFERENCE  //
}
