package moe.ofs.backend.domain.pagination;

import lombok.Data;

import java.util.List;

@Data
public class PageVo<T> {
    // current page number
    private Long current;
    // total number of entries in the data source
    private Long total;
    // result
    private List<T> data;

    public PageVo(Long current, Long total, List<T> data) {
        this.current = current;
        this.total = total;
        this.data = data;
    }
}
