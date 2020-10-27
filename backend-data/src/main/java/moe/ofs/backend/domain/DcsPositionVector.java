package moe.ofs.backend.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DcsPositionVector extends DcsBaseEntity {
    private double x;
    private double y;
    private double z;
}
