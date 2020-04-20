package moe.ofs.backend.object.command;

/*
PAR_10 = 1,
RSBN_5 = 2,
TACAN = 3,
TACAN_TANKER = 4,
ILS_LOCALIZER = 5,
ILS_GLIDESLOPE = 6,
BROADCAST_STATION = 7
*/
public enum BeaconSystem {
    PAR_10(1),
    RSBN_5(2),
    TACAN(3),
    TACAN_TANKER(4),
    ILS_LOCALIZER(5),
    ILS_GLIDESLOPE(6),
    BROADCAST_STATION(7);

    private int type;

    BeaconSystem(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
