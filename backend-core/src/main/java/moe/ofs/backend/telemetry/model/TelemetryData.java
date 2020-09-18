package moe.ofs.backend.telemetry.model;

import lombok.Data;

@Data
public class TelemetryData {
    private long missionStateLuaMemory;
    private long hookStateLuaMemory;
    private long exportStateLuaMemory;
}
