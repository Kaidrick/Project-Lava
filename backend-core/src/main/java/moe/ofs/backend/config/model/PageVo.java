package moe.ofs.backend.config.model;

import lombok.Data;

import java.util.List;

@Data
public class PageVo<T> {
    //    当前页数
    private Long current;
    //    总页数
    private Long total;
    //    是否存在下一页
    private Boolean hasNextPage;
    //    是否存在上一页
    private Boolean hasPreviousPage;
    //    结果
    private List<T> data;

    public PageVo(Long current, Long total, Boolean hasNextPage, Boolean hasPreviousPage, List<T> data) {
        this.current = current;
        this.total = total;
        this.hasNextPage = hasNextPage;
        this.hasPreviousPage = hasPreviousPage;
        this.data = data;
    }
}
