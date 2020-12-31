package moe.ofs.backend.connector.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public abstract class AbstractDataUpdateBundle<T> {
    String action;
    T data;
    boolean is_tail;
}
