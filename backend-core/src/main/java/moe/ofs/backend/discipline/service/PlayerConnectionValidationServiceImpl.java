package moe.ofs.backend.discipline.service;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.discipline.exceptions.ValidatorNotSpecifiedException;
import moe.ofs.backend.discipline.model.ConnectionValidator;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.request.DataRequest;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class PlayerConnectionValidationServiceImpl implements PlayerConnectionValidationService {

    private final RequestTransmissionService requestTransmissionService;

    public PlayerConnectionValidationServiceImpl(RequestTransmissionService requestTransmissionService) {
        this.requestTransmissionService = requestTransmissionService;
    }

    @PostConstruct
    public void setUp() {
        MissionStartObservable missionStartObservable = s -> {
            requestTransmissionService.send(new ServerDataRequest(LuaQueryEnv.SERVER_CONTROL,
                    LuaScripts.load("connection_validation/player_try_connect_hook.lua")));

            log.info("Setting up player connection validation service");
        };
        missionStartObservable.register();
    }

    @Override
    public void addValidator(ConnectionValidator validator) throws ValidatorNotSpecifiedException {
        requestTransmissionService.send(new ServerDataRequest(LuaQueryEnv.SERVER_CONTROL,
                LuaScripts.loadAndPrepare("connection_validation/player_try_connect_hook.lua",
                        this.getClass().getName() + ":" + validator.getName(),
                        validator.getFunction())));
    }

    @Override
    public void removeValidator(ConnectionValidator validator) {

    }

    @Override
    public void removeValidatorByName(String name) {

    }
}
