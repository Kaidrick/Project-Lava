package moe.ofs.backend.security.aspect;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.AdminInfoDto;
import moe.ofs.backend.domain.LavaUserToken;
import moe.ofs.backend.security.annotation.CheckPermission;
import moe.ofs.backend.security.provider.PasswordTypeProvider;
import moe.ofs.backend.security.service.AccessTokenService;
import moe.ofs.backend.security.service.AdminInfoService;
import moe.ofs.backend.security.token.PasswordTypeToken;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
@Slf4j
public class LavaCheckPermissionAspect {
    private final AccessTokenService accessTokenService;
    private final AdminInfoService adminInfoService;
    private final PasswordTypeProvider passwordTypeProvider;

    @Pointcut("@annotation(checkPermission)")
    public void annotatedMethods() {
    }

    @Pointcut("@within(checkPermission)")
    public void annotatedClasses() {
    }

    @Before("annotatedClasses()|| annotatedMethods()")
    public Object checkPermission(JoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        CheckPermission methodAnnotation = signature.getMethod().getAnnotation(CheckPermission.class);
        Class<?> aClass = point.getSignature().getDeclaringType();
        CheckPermission classAnnotation = aClass.getAnnotation(CheckPermission.class);
        CheckPermission checkPermission = methodAnnotation != null ? methodAnnotation : classAnnotation;

        String[] groups = checkPermission.groups();
        String[] nonGroups = checkPermission.nonGroups();
        String[] roles = checkPermission.roles();
        String[] nonRoles = checkPermission.nonRoles();
        if (ObjectUtil.isAllEmpty(groups, nonGroups, roles, nonGroups)) return point.getArgs();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (checkPermission.requiredAccessToken()) {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String accessToken = request.getHeader("access_token");
            if (accessToken == null) throw new BadCredentialsException("accessToken不能为空！");

//            获取token中存储的信息，调用provider校验信息
            boolean b = accessTokenService.checkAccessToken(accessToken);
            if (!b) throw new BadCredentialsException("accessToken已过期，请使用refreshToken刷新");
            LavaUserToken lavaUserToken = accessTokenService.getByAccessToken(accessToken);
            PasswordTypeToken token = (PasswordTypeToken) lavaUserToken.getUserInfoToken();

            // 判断是否重新认证用户信息
            if (!authentication.getName().equals(token.getName())) {
                Authentication authenticate = passwordTypeProvider.authenticate(accessToken);
//            将用户认证信息存入session
                SecurityContextHolder.getContext().setAuthentication(authenticate);
            }
            username = token.getName();
        }

        if (username.equals("anonymousUser")) throw new BadCredentialsException("请先登录！");
        AdminInfoDto adminInfoDto = adminInfoService.getOneByName(username);
        boolean a, b;
        a = checkArray(adminInfoDto.getGroups(), groups, nonGroups);
        b = checkArray(adminInfoDto.getRoles(), roles, nonRoles);

        if (a && b) {
            return point.getArgs();
        } else {
            throw new RuntimeException("无权访问！");
        }
    }

    private boolean checkArray(List<String> test, String[] exist, String[] noExist) {
        if (ArrayUtil.isAllEmpty(exist, noExist)) return true;
        if (ArrayUtil.isEmpty(test)) return ArrayUtil.isEmpty(exist);

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

    private boolean checkNoExist(List<String> test, String[] noExist) {
        Set<String> a = new HashSet<>(test);
        Set<String> c = new HashSet<>(Arrays.asList(noExist));
        a.addAll(c);
        return a.size() == (test.size() + noExist.length);
    }

    private boolean checkExist(List<String> test, String[] exist) {
        if (test.size() < exist.length) return false;
        Set<String> a = new HashSet<>(test);
        Set<String> b = new HashSet<>(Arrays.asList(exist));
        b.retainAll(a);
        return b.size() == exist.length;
    }

}
