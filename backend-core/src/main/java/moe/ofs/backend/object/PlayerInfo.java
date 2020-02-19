package moe.ofs.backend.object;

public class PlayerInfo extends SimObject {
    private static final String emptySlotAltName = "Observer";

    private String ipaddr;
    private String lang;
    private int ping;
    private int side;
    private String slot;
    private boolean started;  // what is this?
    private String ucid;

    @Override
    public String toString() {
        String slot = getSlot();
        return String.format("Player <%s>(%s) Slot <%s> using [%s] Client @ %s", name, ucid, slot, lang.toUpperCase(), ipaddr);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PlayerInfo) {
            PlayerInfo playerInfo = (PlayerInfo) obj;
            return playerInfo.getUcid().equals(getUcid()) && playerInfo.getSlot().equals(getSlot());
        }
        return false;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public String getLang() {
        return lang;
    }

    public int getPing() {
        return ping;
    }

    public int getSide() {
        return side;
    }

    public String getSlot() {
        if(slot.equals("")) {
            return emptySlotAltName;
        }
        return slot;
    }

    public boolean isStarted() {
        return started;
    }

    public String getUcid() {
        return ucid;
    }
}
