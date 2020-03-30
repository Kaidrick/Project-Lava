package moe.ofs.backend.object;

/**
 *  16 : Valid spawn points on runway
 *  40 : Helicopter only spawn
 *  68 : Hardened Air Shelter
 *  72 : Open/Shelter air airplane only
 *  104: Open air spawn
 */

public enum ParkingType {
    RUNWAY(16), HELICOPTER_ONLY(40), SHELTER(68), AIRPLANE_ONLY(72), OPEN_AIR(104);

    private int type;

    ParkingType(int type) {
        this.type = type;
    }
}
