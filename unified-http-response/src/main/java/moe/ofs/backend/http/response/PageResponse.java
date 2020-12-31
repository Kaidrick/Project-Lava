package moe.ofs.backend.http.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageResponse<T> extends Response<T> {
    private Long current;
    private Long total;
}
