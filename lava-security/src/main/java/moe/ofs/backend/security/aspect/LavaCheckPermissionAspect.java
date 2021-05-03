package moe.ofs.backend.security.aspect;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.Permission;
import moe.ofs.backend.dto.BaseUserInfoDto;
import moe.ofs.backend.security.annotation.CheckPermission;
import moe.ofs.backend.security.exception.authorization.InsufficientAccessRightException;
import moe.ofs.backend.security.exception.token.AccessTokenExpiredException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LavaCheckPermissionAspect {

    @Pointcut("@annotation(moe.ofs.backend.security.annotation.CheckPermission)")
    public void annotatedMethods() {
    }

    @Pointcut("@within(moe.ofs.backend.security.annotation.CheckPermission)")
    public void annotatedClasses() {
    }

    @Before("annotatedClasses() || annotatedMethods()")
    public Object checkPermission(JoinPoint point) {

        MethodSignature signature = (MethodSignature) point.getSignature();
        CheckPermission methodAnnotation = signature.getMethod().getAnnotation(CheckPermission.class);
        Class<?> aClass = point.getSignature().getDeclaringType();
        CheckPermission classAnnotation = aClass.getAnnotation(CheckPermission.class);
        Permission permission = getCheckPermission(methodAnnotation, classAnnotation);

        BaseUserInfoDto baseUserInfoDto = new BaseUserInfoDto();

        if (permission.isRequiredAccessToken()) {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            Boolean accessTokenValidate = (Boolean) request.getAttribute("accessTokenValidate");
            if (!accessTokenValidate) throw new AccessTokenExpiredException("AccessToken不存在或已失效");
        }

        Set<String> groups = permission.getGroups();
        Set<String> nonGroups = permission.getNonGroups();
        Set<String> roles = permission.getRoles();
        Set<String> nonRoles = permission.getNonRoles();

        if (ObjectUtil.isAllEmpty(groups, nonGroups, roles, nonGroups))
            return point.getArgs();

        boolean inAllowedGroups, hasAllowedRoles;
        inAllowedGroups = checkArray(baseUserInfoDto.getGroups(), groups, nonGroups);
        hasAllowedRoles = checkArray(baseUserInfoDto.getRoles(), roles, nonRoles);

        if (inAllowedGroups && hasAllowedRoles) {
            return point.getArgs();
        } else {
            throw new InsufficientAccessRightException("无权访问！");
        }
    }

    private Permission getCheckPermission(CheckPermission methodAnnotation, CheckPermission classAnnotation) {
        Permission permission = new Permission();
        addToPermission(classAnnotation, permission);

        addToPermission(methodAnnotation, permission);

        return permission;
    }

    private void addToPermission(CheckPermission methodAnnotation, Permission permission) {
        if (methodAnnotation != null) {
            permission.getGroups().addAll(Arrays.asList(methodAnnotation.groups()));
            permission.getNonGroups().addAll(Arrays.asList(methodAnnotation.nonGroups()));
            permission.getRoles().addAll(Arrays.asList(methodAnnotation.roles()));
            permission.getNonRoles().addAll(Arrays.asList(methodAnnotation.nonRoles()));
            permission.setRequiredAccessToken(methodAnnotation.requiredAccessToken());
        }
    }

    private boolean checkArray(List<String> test, Set<String> exist, Set<String> noExist) {
        if (noExist.isEmpty() && exist.isEmpty()) return true;
        if (test.isEmpty()) return exist.isEmpty();

//        test  noExist不为空
        if (ArrayUtil.isEmpty(exist)) {
            return checkNoExist(test, noExist);
        }
//        test  exist不为空
        if (ArrayUtil.isEmpty(noExist)) {
            return checkExist(test, exist);
        }

//        仨不为空
        boolean flag1, flag2;
        flag1 = checkExist(test, exist);
        flag2 = checkNoExist(test, noExist);
        return flag1 && flag2;
    }

    private boolean checkNoExist(List<String> test, Set<String> noExist) {
        Set<String> a = new HashSet<>(test);
        a.addAll(test);
        return a.size() == (test.size() + noExist.size());
    }

    private boolean checkExist(List<String> test, Set<String> exist) {
        if (test.size() < exist.size()) return false;
        Set<String> a = new HashSet<>(test);
        Set<String> b = new HashSet<>(exist);
        b.retainAll(a);
        return b.size() == exist.size();
    }
}
