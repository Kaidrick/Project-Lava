package moe.ofs.backend.function.spawncontrol.aspects;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpawnControlVo<T> implements Serializable {
    private T object;
    private ControlAction action;
    private long timestamp;
    private boolean success;
}
