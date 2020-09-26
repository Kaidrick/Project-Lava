package moe.ofs.backend.object;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.ofs.backend.domain.BaseEntity;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class TelemetryData extends BaseEntity implements Serializable {
    private double missionStateLuaMemory;

    private double hookStateLuaMemory;

    private double exportStateLuaMemory;

    private Instant timestamp;
}
