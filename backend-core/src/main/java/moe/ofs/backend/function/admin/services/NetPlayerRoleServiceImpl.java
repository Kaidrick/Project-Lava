package moe.ofs.backend.function.admin.services;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import moe.ofs.backend.dao.PlayerRoleAssignmentDao;
import moe.ofs.backend.domain.admin.PlayerRole;
import moe.ofs.backend.domain.admin.RoleAssignment;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.repositories.PlayerRoleRepository;
import moe.ofs.backend.repositories.RoleAssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NetPlayerRoleServiceImpl implements NetPlayerRoleService {

    private final PlayerRoleRepository playerRoleRepository;
    private final RoleAssignmentRepository roleAssignmentRepository;

    private final PlayerRoleAssignmentDao playerRoleAssignmentDao;

    public NetPlayerRoleServiceImpl(PlayerRoleRepository playerRoleRepository, RoleAssignmentRepository roleAssignmentRepository,
                                    PlayerRoleAssignmentDao playerRoleAssignmentDao) {
        this.playerRoleRepository = playerRoleRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.playerRoleAssignmentDao = playerRoleAssignmentDao;

//        List<RoleAssignment> roleAssignments = playerRoleRepository.list(Wrappers.<PlayerRole>lambdaQuery().le(PlayerRole::getRoleLevel, 2))
//                .stream().map(r -> {
//                    RoleAssignment roleAssignment = new RoleAssignment();
//                    roleAssignment.setRoleId(r.getId());
//                    roleAssignment.setUcid("95abc");
//                    roleAssignment.setTime(new Date());
//                    return roleAssignment;
//                }).collect(Collectors.toList());

//        roleAssignmentRepository.saveBatch(roleAssignments);
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
}
