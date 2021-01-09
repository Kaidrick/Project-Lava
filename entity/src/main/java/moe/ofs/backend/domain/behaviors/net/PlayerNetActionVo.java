package moe.ofs.backend.domain.behaviors.net;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlayerNetActionVo<T> implements Serializable {
    private T object;
    private NetAction action;
    private long timestamp;
    private boolean success;
}
