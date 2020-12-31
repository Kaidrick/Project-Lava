package moe.ofs.backend.pagination;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class LavaSystemLogPageObject extends PageObject {
    private Date from;
    private Date to;
    private String keyword;
    private String logLevel;
    private String source;
}
