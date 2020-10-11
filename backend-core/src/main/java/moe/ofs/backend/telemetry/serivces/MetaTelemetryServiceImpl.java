package moe.ofs.backend.telemetry.serivces;

import moe.ofs.backend.dispatcher.model.LavaTask;
import moe.ofs.backend.dispatcher.services.LavaTaskDispatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetaTelemetryServiceImpl implements MetaTelemetryService {

    private final LavaTaskDispatcher dispatcher;

    public MetaTelemetryServiceImpl(LavaTaskDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public int getDispatcherTaskCount() {
        return dispatcher.getTaskCount();
    }

    @Override
    public List<LavaTask> findAllDispatcherTasks() {
        return dispatcher.findAll().stream()
//                .map(LavaTask::toString)
                .collect(Collectors.toList());
    }
}
