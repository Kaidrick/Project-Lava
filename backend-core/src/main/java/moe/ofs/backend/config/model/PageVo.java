package moe.ofs.backend.config.model;

import lombok.Data;

import java.util.List;

@Data
public class PageVo<T> {
    //    当前页数
    private Long current;
    //    总页数
    private Long total;
    //    结果
    private List<T> data;

    public PageVo(Long current, Long total, List<T> data) {
        this.current = current;
        this.total = total;
        this.data = data;
    }
}
