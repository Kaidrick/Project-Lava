package moe.ofs.backend.security.aspect;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.AdminInfo;
import moe.ofs.backend.domain.Permission;
import moe.ofs.backend.dto.AdminInfoDto;
import moe.ofs.backend.security.annotation.CheckPermission;
import moe.ofs.backend.security.exception.authorization.InsufficientAccessRightException;
import moe.ofs.backend.security.exception.token.AccessTokenExpiredException;
import moe.ofs.backend.security.exception.token.InvalidAccessTokenException;
import moe.ofs.backend.security.provider.PasswordTypeProvider;
import moe.ofs.backend.security.service.AccessTokenService;
import moe.ofs.backend.security.service.AdminInfoService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;
import java.util.*;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LavaCheckPermissionAspect {
    private final AccessTokenService accessTokenService;
    private final AdminInfoService adminInfoService;
    private final PasswordTypeProvider passwordTypeProvider;

    @Pointcut("@annotation(moe.ofs.backend.security.annotation.CheckPermission)")
    public void annotatedMethods() {
    }

    @Pointcut("@within(moe.ofs.backend.security.annotation.CheckPermission)")
    public void annotatedClasses() {
    }

    @Around("annotatedClasses() || annotatedMethods()")
    public Object checkPermission(ProceedingJoinPoint point) throws Throwable {

        MethodSignature signature = (MethodSignature) point.getSignature();
        CheckPermission methodAnnotation = signature.getMethod().getAnnotation(CheckPermission.class);
        Class<?> aClass = point.getSignature().getDeclaringType();
        CheckPermission classAnnotation = aClass.getAnnotation(CheckPermission.class);
        Permission permission = getCheckPermission(methodAnnotation, classAnnotation);

        AdminInfoDto adminInfoDto = new AdminInfoDto();
        Authentication authentication = null;

        if (permission.isRequiredAccessToken()) {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String accessToken = request.getHeader("access_token");
            if (StrUtil.isBlank(accessToken)) throw new InvalidAccessTokenException("accessToken不能为空！");

            boolean b = accessTokenService.checkAccessToken(accessToken);
            if (!b) throw new AccessTokenExpiredException("accessToken已过期，请使用refreshToken刷新");
            Object userInfoToken = accessTokenService.getByAccessToken(accessToken).getUserInfoToken();

            if (userInfoToken instanceof AdminInfo) {
                authentication = passwordTypeProvider.authenticate(accessToken);
                adminInfoDto = adminInfoService.getOneByName(authentication.getName());
            }

        }

        Set<String> groups = permission.getGroups();
        Set<String> nonGroups = permission.getNonGroups();
        Set<String> roles = permission.getRoles();
        Set<String> nonRoles = permission.getNonRoles();

        Parameter[] parameters = signature.getMethod().getParameters();
        int index = -1;
        if (parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].getParameterizedType().equals(Authentication.class)) index = i;
            }
        }

        if (ObjectUtil.isAllEmpty(groups, nonGroups, roles, nonGroups))
            return point.proceed(setTargetMethodArgs(point.getArgs(), index, authentication));

        boolean inAllowedGroups, hasAllowedRoles;
        inAllowedGroups = checkArray(adminInfoDto.getGroups(), groups, nonGroups);
        hasAllowedRoles = checkArray(adminInfoDto.getRoles(), roles, nonRoles);

        if (inAllowedGroups && hasAllowedRoles) {
            return point.proceed(setTargetMethodArgs(point.getArgs(), index, authentication));
        } else {
            throw new InsufficientAccessRightException("无权访问！");
        }
    }

    private Permission getCheckPermission(CheckPermission methodAnnotation, CheckPermission classAnnotation) {
        Permission permission = new Permission();
        if (classAnnotation != null) {
            permission.getGroups().addAll(Arrays.asList(classAnnotation.groups()));
            permission.getNonGroups().addAll(Arrays.asList(classAnnotation.nonGroups()));
            permission.getRoles().addAll(Arrays.asList(classAnnotation.roles()));
            permission.getNonRoles().addAll(Arrays.asList(classAnnotation.nonRoles()));
            permission.setRequiredAccessToken(methodAnnotation.requiredAccessToken());
        }

        if (methodAnnotation != null) {
            permission.getGroups().addAll(Arrays.asList(methodAnnotation.groups()));
            permission.getNonGroups().addAll(Arrays.asList(methodAnnotation.nonGroups()));
            permission.getRoles().addAll(Arrays.asList(methodAnnotation.roles()));
            permission.getNonRoles().addAll(Arrays.asList(methodAnnotation.nonRoles()));
            permission.setRequiredAccessToken(methodAnnotation.requiredAccessToken());
        }

        return permission;
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

    private Object[] setTargetMethodArgs(Object[] args, int index, Authentication authentication) {
        if (args == null || index == -1) {
            return args;
        }

        List<Object> objects = Arrays.asList(args);
        if (objects.get(index) == null) objects.set(index, authentication);
        return objects.toArray();
    }
}
