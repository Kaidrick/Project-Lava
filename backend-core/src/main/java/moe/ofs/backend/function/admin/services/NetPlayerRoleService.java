package moe.ofs.backend.function.admin.services;

import moe.ofs.backend.domain.admin.PlayerRole;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;

import java.util.Set;

/**
 * ucid paired with a role for player
 */
public interface NetPlayerRoleService {
    void assignRole(String ucid, PlayerRole role);

    void removeRole(String ucid, PlayerRole role);

    void assignRole(PlayerInfo playerInfo, PlayerRole role);

    void removeRole(PlayerInfo playerInfo, PlayerRole role);

    Set<String> findUcidsWithRole(PlayerRole role);

    Set<PlayerRole> findPlayerRoles(String ucid);

    Set<PlayerRole> findPlayerRoles(PlayerInfo playerInfo);
}
