package moe.ofs.backend.telemetry.serivces;

import moe.ofs.backend.object.TelemetryData;
import moe.ofs.backend.request.DataRequest;
import moe.ofs.backend.services.map.AbstractMapService;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class LuaStateTelemetryMapService extends AbstractMapService<TelemetryData>
        implements LuaStateTelemetryService {

    @Value("${telemetry.data.size}")
    private long maxTelemetryEntrySize;

    private static final String TELEMETRY_SCRIPT_PATH = "telemetry/memory_usage.lua";

    /**
     * Should only save 1,000 entries by default
     * If overflowed, delete by id -> current id minus 1000
     */
    @Override
    public void recordTelemetry() {
        // fetch telemetry data for each lua state
        // user should be able to choose from which lua state the data is stored
        // user should also have a option to right memory usage to log file
        TelemetryData.TelemetryDataBuilder builder = TelemetryData.builder();

        double missionStateLuaMem = Double.parseDouble(
                LuaScripts.requestWithFile(LuaQueryEnv.MISSION_SCRIPTING, TELEMETRY_SCRIPT_PATH).get());

        double hookStateLuaMem = Double.parseDouble(
                LuaScripts.requestWithFile(LuaQueryEnv.SERVER_CONTROL, TELEMETRY_SCRIPT_PATH).get());

        double exportStateLuaMem = Double.parseDouble(
                LuaScripts.requestWithFile(LuaQueryEnv.EXPORT, TELEMETRY_SCRIPT_PATH).get());

        builder.missionStateLuaMemory(missionStateLuaMem)
               .hookStateLuaMemory(hookStateLuaMem)
               .exportStateLuaMemory(exportStateLuaMem)
               .timestamp(Instant.now());

        TelemetryData data = builder.build();

        save(data);  // save current entry first

        if (map.size() > maxTelemetryEntrySize) {
            deleteById(getNextId() - maxTelemetryEntrySize - 1);  // delete overflowed entry
        }
    }
}
