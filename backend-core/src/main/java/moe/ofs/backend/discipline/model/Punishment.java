package moe.ofs.backend.discipline.model;

public enum Punishment {
    WARNING,

    DESTORY, EXPLOSION,

    KICK, BAN, PERMANENT_BAN;

    public int getType() {
        return this.ordinal();
    }

    public String getTypeName() {
        return this.name();
    }
}
