package moe.ofs.backend.http;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageResponse<T> extends Response<T> {
    private Long current;
    private Long total;
}
