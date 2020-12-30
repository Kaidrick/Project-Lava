package moe.ofs.backend.discipline.service.impl;

import moe.ofs.backend.discipline.service.PlayerDisciplineService;
import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.function.spawncontrol.DestructiveService;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PlayerDisciplineServiceImpl implements PlayerDisciplineService {
    private static final int DEFAULT_BAN_SECONDS = Integer.MAX_VALUE;
    private static final String DEFAULT_BAN_REASON = "Server specified no reason for this ban.";

    private final RequestTransmissionService requestTransmissionService;
    private final DestructiveService destructiveService;

    public PlayerDisciplineServiceImpl(RequestTransmissionService requestTransmissionService,
                                       DestructiveService destructiveService) {
        this.requestTransmissionService = requestTransmissionService;
        this.destructiveService = destructiveService;
    }

    @Override
    public void ban(PlayerInfo player) {
        String luaString = LuaScripts.loadAndPrepare("api/ban_net_player.lua",
                player.getNetId(), DEFAULT_BAN_SECONDS, DEFAULT_BAN_REASON);
        requestTransmissionService.send(
                new ServerExecRequest(LuaQueryEnv.SERVER_CONTROL, luaString)
        );
    }

    @Override
    public void ban(PlayerInfo player, Duration duration) {
        String luaString = LuaScripts.loadAndPrepare("api/ban_net_player.lua",
                player.getNetId(), duration.getSeconds(), DEFAULT_BAN_REASON);
        requestTransmissionService.send(
                new ServerExecRequest(LuaQueryEnv.SERVER_CONTROL, luaString)
        );
    }

    @Override
    public void ban(PlayerInfo player, String reason) {
        String luaString = LuaScripts.loadAndPrepare("api/ban_net_player.lua",
                player.getNetId(), DEFAULT_BAN_SECONDS, reason);
        requestTransmissionService.send(new ServerExecRequest(LuaQueryEnv.SERVER_CONTROL, luaString));
    }

    @Override
    public void ban(PlayerInfo player, String reason, Duration duration) {
        String luaString = LuaScripts.loadAndPrepare("api/ban_net_player.lua",
                player.getNetId(), duration.getSeconds(), reason);
        requestTransmissionService.send(
                new ServerExecRequest(LuaQueryEnv.SERVER_CONTROL, luaString)
        );
    }

    @Override
    public void kick(PlayerInfo player) {
        String luaString = LuaScripts.loadAndPrepare("api/kick_net_player.lua",
                player.getNetId(), "Server specifies no kick reason.");
        requestTransmissionService.send(
                new ServerExecRequest(LuaQueryEnv.SERVER_CONTROL, luaString)
        );
    }

    @Override
    public void kick(PlayerInfo player, String reason) {
        String luaString = LuaScripts.loadAndPrepare("api/kick_net_player.lua",
                player.getNetId(), !reason.equals("") ? reason : "Server specifies no kick reason.");
        requestTransmissionService.send(
                new ServerExecRequest(LuaQueryEnv.SERVER_CONTROL, luaString)
        );
    }

    @Override
    public void destroy(PlayerInfo playerInfo) {
        destructiveService.destroy(playerInfo);
    }
}
