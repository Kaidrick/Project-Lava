package moe.ofs.backend.function.spawncontrol.services.impl;

import moe.ofs.backend.LavaLog;
import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.function.spawncontrol.services.DestructiveService;
import moe.ofs.backend.object.FlyableUnit;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.ExportObjectService;
import moe.ofs.backend.services.FlyableUnitService;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DestructiveServiceImpl implements DestructiveService {

    private final PlayerInfoService playerInfoService;
    private final ExportObjectService exportObjectService;

    private final FlyableUnitService flyableUnitService;

    private final RequestTransmissionService requestTransmissionService;

    private final LavaLog.Logger logger = LavaLog.getLogger(DestructiveServiceImpl.class);

    private static final String DESTROY = LuaScripts.load("spawn_control/remove_object_by_runtime_id.lua");

    public DestructiveServiceImpl(PlayerInfoService playerInfoService, ExportObjectService exportObjectService,
                                  FlyableUnitService flyableUnitService,
                                  RequestTransmissionService requestTransmissionService) {
        this.playerInfoService = playerInfoService;
        this.exportObjectService = exportObjectService;
        this.flyableUnitService = flyableUnitService;
        this.requestTransmissionService = requestTransmissionService;
    }

    @Override
    public void destroy(ExportObject exportObject) {
        requestTransmissionService.send(
                new ServerDataRequest(String.format(DESTROY, exportObject.getRuntimeID()))
                        .addProcessable(s ->
                                logger.info(String.format("World removed object (ID: %d, Type: %s) " +
                                                "from current mission.",
                                        exportObject.getRuntimeID(), exportObject.getName())))
        );
    }

    /**
     * Search the runtime id of the ExportObject that matches the unit name of player slot id unit name.
     * @param playerInfo the player who is occupying the unit that should be destroyed.
     */
    @Override
    public boolean destroy(PlayerInfo playerInfo) {
        Optional<FlyableUnit> flyableUnitOptional = flyableUnitService.findByUnitId(playerInfo.getSlot());
        if (flyableUnitOptional.isPresent()) {
            Optional<ExportObject> exportObjectOptional = exportObjectService.findByUnitName(playerInfo.getName());
            if (exportObjectOptional.isPresent()) {
                ExportObject exportObject = exportObjectOptional.get();

                boolean destroyed = LuaScripts.requestWithFile(LuaQueryEnv.MISSION_SCRIPTING,
                        "spawn_control/remove_object_by_runtime_id.lua", exportObject.getRuntimeID()).getAsBoolean();

                if(destroyed) {
                    logger.info(String.format("World removed object (ID: %d, Type: %s; " +
                                    "linked player: %s[%s]<%s>) " +
                                    "from current mission.",
                            exportObject.getRuntimeID(), exportObject.getName(),
                            playerInfo.getName(), playerInfo.getLang(), playerInfo.getIpaddr()));
                } else {
                    logger.info(String.format("World failed to removed object " +
                                    "because this object does not exist in mission: " +
                                    "(ID: %d, Type: %s; " +
                                    "linked player: %s[%s]<%s>).",
                            exportObject.getRuntimeID(), exportObject.getName(),
                            playerInfo.getName(), playerInfo.getLang(), playerInfo.getIpaddr()));
                }
            } else {
                return false;
            }
        }

        return false;
    }

    @Override
    public void explode(ExportObject exportObject) {

    }

    @Override
    public void explode(PlayerInfo playerInfo) {

    }
}
