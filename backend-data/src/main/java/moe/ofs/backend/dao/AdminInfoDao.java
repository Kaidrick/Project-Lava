package moe.ofs.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import moe.ofs.backend.domain.AdminInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AdminInfoDao extends BaseMapper<AdminInfo> {

    @Select("SELECT role_info.role 'roles' FROM group_role,role_info WHERE group_role.role_id = role_info.id AND " +
            "group_role.group_id IN ( SELECT user_group.group_id FROM user_group WHERE user_group.user_id = ${id});")
    List<String> selectRoles(@Param("id") Long id);

    @Select("SELECT group_info.`group` 'groups' FROM user_group,group_info WHERE user_group.group_id = group_info.id " +
            "AND user_group.user_id = ${id};")
    List<String> selectGroups(@Param("id") Long id);

    @Select("SELECT admin_info.`name`,admin_info.id FROM admin_info,token_info WHERE admin_info.id = token_info.user_id " +
            "AND token_info.access_token='${accessToken}';")
    AdminInfo selectOneByAccessToken(@Param("accessToken") String accessToken);
}
