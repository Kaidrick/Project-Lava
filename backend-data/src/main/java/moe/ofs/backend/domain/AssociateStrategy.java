package moe.ofs.backend.domain;

public enum AssociateStrategy {
    /**
     * Every SimEvent must be associate with an ExportObject in map service or in graveyard service.
     * SimEventService will retry iterating the limbo list in order to find a match.
     * If an association cannot be made after given number of tries, it will query dcs for a runtime id.
     */
    MANDATORY,

    /**
     * If after the given limitation of number of tries, an association is still not present,
     * this sim event will be dropped.
     */
    TRY_WITH_LIMIT,


    MUTUAL_REFERENCE  //
}
