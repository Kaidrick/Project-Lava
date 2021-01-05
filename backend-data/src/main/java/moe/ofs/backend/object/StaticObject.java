package moe.ofs.backend.object;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class StaticObject extends DisplayableObject {
    private int country_id;

    {
        category = Category.STATIC.getCategory();
    }

    @Builder
    public StaticObject(int id, String name, double x, double y, String livery_id, String type,
                        String onboard_num, Double heading, int country_id) {
        super(x, y, livery_id, type, onboard_num, heading);
        this.id = id;
        this.name = name;
        this.country_id = country_id;
    }
}
