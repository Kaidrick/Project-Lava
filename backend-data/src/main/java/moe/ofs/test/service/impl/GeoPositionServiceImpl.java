package moe.ofs.test.service.impl;

import moe.ofs.test.dao.GeoPositionDao;
import moe.ofs.test.entity.GeoPosition;
import moe.ofs.test.service.GeoPositionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Tyler
 * @since 2020-10-19
 */
@Service
public class GeoPositionServiceImpl extends ServiceImpl<GeoPositionDao, GeoPosition> implements GeoPositionService {

}
