package moe.ofs.backend.object.unitofmeasure;

public enum Length {
    METERS("m", "meter", "meters"),
    FEET("ft", "foot", "feet"),
    MILES("mi", "mile", "miles");

    private String symbol;
    private String singular;
    private String plural;

    Length(String symbol, String singular, String plural) {
        this.symbol = symbol;
        this.singular = singular;
        this.plural = plural;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
