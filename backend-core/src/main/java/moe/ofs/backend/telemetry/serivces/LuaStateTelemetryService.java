package moe.ofs.backend.telemetry.serivces;

import moe.ofs.backend.object.TelemetryState;

/**
 * This interface describes the services that can be applied to DCS Lua environment.
 * Planned telemetry includes lua memory usage in three different states, lua request response time, and request count.
 *
 * Telemetry data should be saved to database, thus needing an ORM mapping
 */

public interface LuaStateTelemetryService {
    void recordTelemetry();


}
