package moe.ofs.backend.admin.service;

import moe.ofs.backend.domain.PlayerInfo;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.util.LuaScripts;

import java.time.Duration;

public class PlayerBanKickServiceImpl implements PlayerBanKickService {
    @Override
    public void ban(PlayerInfo player) {

    }

    @Override
    public void ban(PlayerInfo player, String reason) {

    }

    @Override
    public void ban(PlayerInfo player, String reason, Duration duration) {

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
