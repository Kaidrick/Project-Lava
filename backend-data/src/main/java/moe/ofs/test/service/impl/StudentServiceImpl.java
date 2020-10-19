package moe.ofs.test.service.impl;

import moe.ofs.test.dao.StudentDao;
import moe.ofs.test.entity.Student;
import moe.ofs.test.service.StudentService;
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
public class StudentServiceImpl extends ServiceImpl<StudentDao, Student> implements StudentService {

}
