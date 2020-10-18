package moe.ofs.backend.mapper;

import moe.ofs.backend.domain.LavaSystemLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LavaSystemLogMapper {

    LavaSystemLog selectById(Long id);

    List<LavaSystemLog> selectAll();
}
