package moe.ofs.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import moe.ofs.backend.domain.DcsExportObject;

public interface DcsExportObjectDao extends BaseMapper<DcsExportObject> {

    DcsExportObject findById(Long id);

    DcsExportObject findWithPosById(Long id);
}
