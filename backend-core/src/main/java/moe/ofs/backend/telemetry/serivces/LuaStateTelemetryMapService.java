package moe.ofs.backend.telemetry.serivces;

import moe.ofs.backend.object.TelemetryData;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.export.ExportDataRequest;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.map.AbstractMapService;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LuaStateTelemetryMapService
        extends AbstractMapService<TelemetryData>
        implements LuaStateTelemetryService {

    @Value("${telemetry.data.size}")
    private long maxTelemetryEntrySize;

    private final RequestTransmissionService requestTransmissionService;

    public LuaStateTelemetryMapService(RequestTransmissionService requestTransmissionService) {
        this.requestTransmissionService = requestTransmissionService;
    }

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
        double missionStateLuaMem = Double.parseDouble(((ServerDataRequest) requestTransmissionService.send(
                new ServerDataRequest(LuaScripts.load("telemetry/memory_usage.lua"))
        )).get());
        double hookStateLuaMem = Double.parseDouble(((ServerDataRequest) requestTransmissionService.send(
                new ServerDataRequest(RequestToServer.State.DEBUG,
                        LuaScripts.load("telemetry/memory_usage.lua"))
        )).get());
        double exportStateLuaMem = Double.parseDouble(((ExportDataRequest) requestTransmissionService.send(
                new ExportDataRequest(LuaScripts.load("telemetry/memory_usage.lua"))
        )).get());

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
