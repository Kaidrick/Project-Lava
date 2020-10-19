package moe.ofs.test.service.impl;

import moe.ofs.test.dao.TypeDao;
import moe.ofs.test.entity.Type;
import moe.ofs.test.service.TypeService;
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
public class TypeServiceImpl extends ServiceImpl<TypeDao, Type> implements TypeService {

}
