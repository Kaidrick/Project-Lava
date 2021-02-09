package moe.ofs.backend.security.aspect;

import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import moe.ofs.backend.dao.AdminInfoDao;
import moe.ofs.backend.security.annotation.CheckPermission;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @projectName: Project-Lava
 * @className: SecurityAspect
 * @description:
 * @author: alexpetertyler
 * @date: 2021/2/9
 * @version: v1.0
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LavaCheckPermissionAspect {
    private final AdminInfoDao adminInfoDao;

    @Before("@annotation(moe.ofs.backend.security.annotation.CheckPermission)")
    public Object[] checkPermission(JoinPoint point) {
        //fixme 完善切面

        CheckPermission annotation = point.getTarget().getClass().getAnnotation(CheckPermission.class);

        if (annotation.requiredAccessToken()) {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
//            String accessToken = request.getHeader("access_token");

        }
        String[] groups = annotation.groups();
        String[] nonGroups = annotation.nonGroups();
        String[] roles = annotation.roles();
        String[] nonRoles = annotation.nonRoles();

        if (ObjectUtil.isAllEmpty(groups, nonGroups, roles, nonGroups)) return point.getArgs();

        boolean a, b, c, d;


        if (a == b == c == d) {
            return point.getArgs();
        } else {
            throw new RuntimeException("无权访问！");
        }
    }

}
