package moe.ofs.backend.services.test;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.mizdb.AbstractPersistentKeyValueService;
import moe.ofs.backend.services.mizdb.Environment;
import moe.ofs.backend.services.mizdb.InjectionEnvironment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@InjectionEnvironment(Environment.HOOK)
public class TestPersistentKeyValueServiceImpl extends AbstractPersistentKeyValueService<String> {
    public TestPersistentKeyValueServiceImpl(RequestTransmissionService requestTransmissionService) {
        super(requestTransmissionService);
    }

    @PostConstruct
    public void init() {
        MissionStartObservable missionStartObservable = s -> {
            createRepository();
        };
        missionStartObservable.register();

        log.info("TestHookKeyValueServiceImpl init registered");
    }

    @Override
    public String getRepositoryName() {
        return this.getClass().getName();
    }

    @Override
    public List<String> saveAll(Map<Object, String> map) {
        return null;
    }

    @Override
    public void precache() {

    }
}
