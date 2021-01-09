package moe.ofs.backend.object;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class DisplayableObject extends SimObject {
    protected double x;
    protected double y;
    protected String livery_id = "default_livery";
    protected String type;
    protected String onboard_num = "000";
    protected Double heading;
}
