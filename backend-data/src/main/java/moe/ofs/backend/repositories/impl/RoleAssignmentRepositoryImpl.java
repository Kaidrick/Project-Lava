package moe.ofs.backend.repositories.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import moe.ofs.backend.dao.RoleAssignmentDao;
import moe.ofs.backend.domain.admin.RoleAssignment;
import moe.ofs.backend.repositories.RoleAssignmentRepository;
import org.springframework.stereotype.Repository;

@Repository
public class RoleAssignmentRepositoryImpl extends ServiceImpl<RoleAssignmentDao, RoleAssignment>
        implements RoleAssignmentRepository {
}
