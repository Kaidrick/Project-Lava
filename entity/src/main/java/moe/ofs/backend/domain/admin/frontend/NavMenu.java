package moe.ofs.backend.domain.admin.frontend;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import moe.ofs.backend.domain.dcs.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class NavMenu extends BaseEntity {
    private String name;
    private String path;
    private Long pid;
    private boolean leaf;
    private String ident;
    private int ordinal;
}
