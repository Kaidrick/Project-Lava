package moe.ofs.backend.function.admin.services;

import moe.ofs.backend.domain.admin.PlayerRole;
import moe.ofs.backend.domain.admin.PlayerRoleGroup;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;

import java.util.List;
import java.util.Set;

/**
 * ucid paired with a role for player
 */
public interface NetPlayerRoleService {
    void assignRole(String ucid, PlayerRole role);

    void deleteRole(String ucid, PlayerRole role);

    void assignRole(PlayerInfo playerInfo, PlayerRole role);

    void deleteRole(PlayerInfo playerInfo, PlayerRole role);

    Set<String> findUcidsWithRole(PlayerRole role);

    Set<PlayerRole> findPlayerRoles(String ucid);

    Set<PlayerRole> findPlayerRoles(PlayerInfo playerInfo);

    boolean addRole(PlayerRole playerRole);

    boolean deleteRole(PlayerRole playerRole);

    boolean deleteRoleById(Long id);

    List<PlayerRoleGroup> findAllRoleGroup();

    PlayerRoleGroup assignRoleGroup(PlayerInfo playerInfo, PlayerRoleGroup group);

    PlayerRoleGroup assignRoleGroup(String ucid, PlayerRoleGroup group);

    PlayerRoleGroup findPlayerRoleGroup(PlayerInfo playerInfo);

    PlayerRoleGroup findPlayerRoleGroup(String ucid);

    boolean checkRole(String ucid, int roleLevel);

    PlayerRoleGroup findRoleGroupById(Long id);

    PlayerRoleGroup findRoleGroupByName(String name);

    boolean addPlayerRoleGroup(PlayerRoleGroup group);

    boolean deletePlayerRoleGroup(PlayerRoleGroup group);

    boolean deletePlayerRoleGroupById(Long id);
}
