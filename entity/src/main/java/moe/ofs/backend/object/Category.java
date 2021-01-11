package moe.ofs.backend.object;

public enum Category {
    UNIT(1), WEAPON(2), STATIC(3), BASE(4), SCENERY(5), CARGO(6);

    private final int category;

    Category(int category) {
        this.category = category;
    }

    public int getCategory() {
        return category;
    }
}
