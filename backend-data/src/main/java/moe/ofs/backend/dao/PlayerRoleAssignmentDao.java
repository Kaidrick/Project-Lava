package moe.ofs.backend.dao;

import moe.ofs.backend.domain.admin.PlayerRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlayerRoleAssignmentDao {
    List<PlayerRole> findPlayerRolesByUcid(@Param("ucid") String ucid);
}
