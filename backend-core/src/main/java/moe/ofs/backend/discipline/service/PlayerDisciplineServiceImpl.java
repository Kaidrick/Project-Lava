package moe.ofs.backend.discipline.service;

import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PlayerDisciplineServiceImpl implements PlayerDisciplineService {
    private static final int DEFAULT_BAN_SECONDS = Integer.MAX_VALUE;
    private static final String DEFAULT_BAN_REASON = "Server specified no reason for this ban.";

    @Override
    public void ban(PlayerInfo player) {
        String luaString = LuaScripts.loadAndPrepare("api/ban_net_player.lua",
                player.getNetId(), DEFAULT_BAN_SECONDS, DEFAULT_BAN_REASON);
        new ServerExecRequest(RequestToServer.State.DEBUG, luaString).send();
    }

    @Override
    public void ban(PlayerInfo player, Duration duration) {
        String luaString = LuaScripts.loadAndPrepare("api/ban_net_player.lua",
                player.getNetId(), duration.getSeconds(), DEFAULT_BAN_REASON);
        new ServerExecRequest(RequestToServer.State.DEBUG, luaString).send();
    }

    @Override
    public void ban(PlayerInfo player, String reason) {
        String luaString = LuaScripts.loadAndPrepare("api/ban_net_player.lua",
                player.getNetId(), DEFAULT_BAN_SECONDS, reason);
        new ServerExecRequest(RequestToServer.State.DEBUG, luaString).send();
    }

    @Override
    public void ban(PlayerInfo player, String reason, Duration duration) {
        String luaString = LuaScripts.loadAndPrepare("api/ban_net_player.lua",
                player.getNetId(), duration.getSeconds(), reason);
        new ServerExecRequest(RequestToServer.State.DEBUG, luaString).send();
    }

    @Override
    public void kick(PlayerInfo player) {
        String luaString = LuaScripts.loadAndPrepare("api/kick_net_player.lua",
                player.getNetId(), "Server specifies no kick reason.");
        new ServerExecRequest(RequestToServer.State.DEBUG, luaString).send();
    }

    @Override
    public void kick(PlayerInfo player, String reason) {
        String luaString = LuaScripts.loadAndPrepare("api/kick_net_player.lua",
                player.getNetId(), !reason.equals("") ? reason : "Server specifies no kick reason.");
        new ServerExecRequest(RequestToServer.State.DEBUG, luaString).send();
    }
}
