package moe.ofs.backend.function.admin.services;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import moe.ofs.backend.dao.PlayerRoleAssignmentDao;
import moe.ofs.backend.domain.admin.PlayerRole;
import moe.ofs.backend.domain.admin.PlayerRoleGroup;
import moe.ofs.backend.domain.admin.RoleAssignment;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.repositories.PlayerRoleGroupRepository;
import moe.ofs.backend.repositories.PlayerRoleRepository;
import moe.ofs.backend.repositories.RoleAssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NetPlayerRoleServiceImpl implements NetPlayerRoleService {

    private final PlayerRoleRepository playerRoleRepository;
    private final RoleAssignmentRepository roleAssignmentRepository;
    private final PlayerRoleGroupRepository playerRoleGroupRepository;
    private final PlayerRoleAssignmentDao playerRoleAssignmentDao;

    // TODO: temporary map repository for ucid - player role group
    private final Map<String, PlayerRoleGroup> ucidPlayerRoleGroupRepository = new HashMap<>();

    public NetPlayerRoleServiceImpl(PlayerRoleRepository playerRoleRepository, RoleAssignmentRepository roleAssignmentRepository,
                                    PlayerRoleGroupRepository playerRoleGroupRepository, PlayerRoleAssignmentDao playerRoleAssignmentDao) {
        this.playerRoleRepository = playerRoleRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.playerRoleGroupRepository = playerRoleGroupRepository;
        this.playerRoleAssignmentDao = playerRoleAssignmentDao;
    }

    @Override
    public void assignRole(String ucid, PlayerRole role) {
        RoleAssignment assignment = new RoleAssignment();
        assignment.setUcid(ucid);
        assignment.setRoleId(role.getId());
        assignment.setTime(new Date());
        roleAssignmentRepository.save(assignment);
    }

    @Override
    public void deleteRole(String ucid, PlayerRole role) {
        roleAssignmentRepository.remove(Wrappers.<RoleAssignment>lambdaQuery()
                .eq(RoleAssignment::getRoleId, role.getId()).eq(RoleAssignment::getUcid, ucid));
    }

    @Override
    public void assignRole(PlayerInfo playerInfo, PlayerRole role) {
        RoleAssignment assignment = new RoleAssignment();
        assignment.setUcid(playerInfo.getUcid());
        assignment.setRoleId(role.getId());
        assignment.setTime(new Date());
        roleAssignmentRepository.save(assignment);
    }

    @Override
    public void deleteRole(PlayerInfo playerInfo, PlayerRole role) {
        roleAssignmentRepository.remove(Wrappers.<RoleAssignment>lambdaQuery()
                .eq(RoleAssignment::getRoleId, role.getId()).eq(RoleAssignment::getUcid, playerInfo.getUcid()));
    }

    @Override
    public Set<String> findUcidsWithRole(PlayerRole role) {
        return roleAssignmentRepository.list(Wrappers.<RoleAssignment>lambdaQuery()
                .eq(RoleAssignment::getRoleId, role.getId())).stream()
                .map(RoleAssignment::getUcid)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PlayerRole> findPlayerRoles(String ucid) {
        return new HashSet<>(playerRoleAssignmentDao.findPlayerRolesByUcid(ucid));
//                roleAssignmentRepository.list(Wrappers.<RoleAssignment>lambdaQuery()
//                .eq(RoleAssignment::getUcid, ucid)).stream()
//                .map();
    }

    @Override
    public Set<PlayerRole> findPlayerRoles(PlayerInfo playerInfo) {
        return new HashSet<>(playerRoleAssignmentDao.findPlayerRolesByUcid(playerInfo.getUcid()));
    }

    @Override
    public boolean addRole(PlayerRole playerRole) {
        return playerRoleRepository.save(playerRole);
    }

    @Override
    public boolean deleteRole(PlayerRole playerRole) {
        return playerRoleRepository.removeById(playerRole.getId());
    }

    @Override
    public boolean deleteRoleById(Long id) {
        return playerRoleRepository.removeById(id);
    }

    @Override
    public PlayerRoleGroup assignRoleGroup(PlayerInfo playerInfo, PlayerRoleGroup group) {
        return ucidPlayerRoleGroupRepository.put(playerInfo.getUcid(), group);
    }

    @Override
    public PlayerRoleGroup assignRoleGroup(String ucid, PlayerRoleGroup group) {
        return ucidPlayerRoleGroupRepository.put(ucid, group);
    }

    @Override
    public PlayerRoleGroup findPlayerRoleGroup(PlayerInfo playerInfo) {
        return ucidPlayerRoleGroupRepository.get(playerInfo.getUcid());
    }

    @Override
    public PlayerRoleGroup findPlayerRoleGroup(String ucid) {
        return ucidPlayerRoleGroupRepository.get(ucid);
    }

    @Override
    public boolean checkRole(String ucid, int roleLevel) {
        PlayerRoleGroup group = ucidPlayerRoleGroupRepository.get(ucid);
        if (group != null) {
            return group.getRoles().stream().anyMatch(r -> r.getRoleLevel() == roleLevel);
        } else {
            return false;
        }
    }

    @Override
    public PlayerRoleGroup findRoleGroupById(Long id) {
        return playerRoleGroupRepository.getById(id);
    }

    @Override
    public PlayerRoleGroup findRoleGroupByName(String name) {
        return playerRoleGroupRepository.findRolesGroupByName(name);
    }

    @Override
    public boolean addPlayerRoleGroup(PlayerRoleGroup group) {
        return playerRoleGroupRepository.save(group);
    }

    @Override
    public boolean deletePlayerRoleGroup(PlayerRoleGroup group) {
        return playerRoleGroupRepository.removeById(group.getId());
    }

    @Override
    public boolean deletePlayerRoleGroupById(Long id) {
        return playerRoleGroupRepository.removeById(id);
    }
}
