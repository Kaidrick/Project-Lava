package moe.ofs.backend.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class LavaSystemLog extends BaseEntity {
    private String level;
    private String message;
    private String sourceName;
    private Date date;
}
