package moe.ofs.backend.domain.pagination;

import lombok.Data;

@Data
public class PageObject {
    private Long currentPageNo;
    private Integer pageSize;
}
