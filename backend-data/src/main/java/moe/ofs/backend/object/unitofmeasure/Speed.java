package moe.ofs.backend.object.unitofmeasure;

public enum Speed {
    METERS_PER_SECOND("m/s", "meter per second", "meters per second"),
    MILES_PER_HOUR("mph", "mile per hour", "miles per hour"),
    KNOTS("kt", "knot", "knots"),
    KILOMETERS_PER_HOUR("km/h", "kilometer per hour", "kilometers per hour");

    private String symbol;
    private String singular;
    private String plural;

    Speed(String symbol, String singular, String plural) {
        this.symbol = symbol;
        this.singular = singular;
        this.plural = plural;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
