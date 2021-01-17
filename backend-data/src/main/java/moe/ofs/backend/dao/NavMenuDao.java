package moe.ofs.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import moe.ofs.backend.domain.admin.frontend.NavMenu;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NavMenuDao extends BaseMapper<NavMenu> {
}
