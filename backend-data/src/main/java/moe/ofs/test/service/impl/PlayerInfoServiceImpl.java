package moe.ofs.test.service.impl;

import moe.ofs.test.dao.PlayerInfoDao;
import moe.ofs.test.entity.PlayerInfo;
import moe.ofs.test.service.PlayerInfoService;
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
public class PlayerInfoServiceImpl extends ServiceImpl<PlayerInfoDao, PlayerInfo> implements PlayerInfoService {

}
