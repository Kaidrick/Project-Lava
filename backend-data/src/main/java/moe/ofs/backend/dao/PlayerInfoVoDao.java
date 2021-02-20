package moe.ofs.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import moe.ofs.backend.vo.PlayerInfoVo;
import org.apache.ibatis.annotations.Param;

public interface PlayerInfoVoDao extends BaseMapper<PlayerInfoVo> {
    PlayerInfoVo getOneByNetIdAndName(@Param("net_id") Integer netId, @Param("name") String name);
}
