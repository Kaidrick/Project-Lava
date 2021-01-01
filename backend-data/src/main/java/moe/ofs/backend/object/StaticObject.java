package moe.ofs.backend.object;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StaticObject extends DisplayableObject {
    private int country_id;

    {
        category = Category.STATIC.getCategory();
    }

    @Builder
    public StaticObject(String name, double x, double y, String livery_id, String type,
                        String onboard_num, Double heading, int country_id) {
        super(x, y, livery_id, type, onboard_num, heading);
        this.name = name;
        this.country_id = country_id;
    }
}
