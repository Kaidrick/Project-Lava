package moe.ofs.backend.domain.dcs.theater;

public enum Orientation {
    NORTH(1,"N"), SOUTH(-1,"S"), EAST(1,"E"), WEST(-1,"W");

    Orientation(int sign, String symbol) {
        this.sign = sign;
        this.symbol = symbol;
    }

    private int sign;
    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    public int getSign() {
        return sign;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
