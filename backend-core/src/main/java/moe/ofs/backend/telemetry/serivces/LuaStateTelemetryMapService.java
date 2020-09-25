package moe.ofs.backend.telemetry.serivces;

import moe.ofs.backend.object.TelemetryData;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.export.ExportDataRequest;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.services.map.AbstractMapService;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LuaStateTelemetryMapService
        extends AbstractMapService<TelemetryData>
        implements LuaStateTelemetryService {

    /**
     * Should only save 1,000 entries by default
     * If overflowed, delete by id -> current id minus 1000
     */
    @Override
    public void recordTelemetry() {
        // fetch telemetry data for each lua state
        // user should be able to choose from which lua state the data is stored
        // user should also have a option to right memory usage to log file

        TelemetryData data = TelemetryData.builder()
                .missionStateLuaMemory(Double.parseDouble(((ServerDataRequest) new ServerDataRequest(
                                LuaScripts.load("telemetry/memory_usage")).send()).get()))

                .hookStateLuaMemory(Double.parseDouble(
                        ((ServerDataRequest) new ServerDataRequest(RequestToServer.State.DEBUG,
                                LuaScripts.load("telemetry/memory_usage"))
                                .send()).get()))

                .exportStateLuaMemory(Double.parseDouble(
                        ((ExportDataRequest) new ExportDataRequest(
                                LuaScripts.load("telemetry/memory_usage"))
                                .send()).get()))

                .timestamp(LocalDateTime.now())
                .build();

        save(data);  // save current entry first

        if (map.size() > 1000) {
            deleteById(getNextId() - 1000);  // delete overflowed entry
        }

    }
}
