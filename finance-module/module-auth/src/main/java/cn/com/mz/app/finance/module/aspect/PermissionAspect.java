package cn.com.mz.app.finance.module.aspect;

import cn.com.mz.app.finance.common.exceptions.BusinessException;
import cn.com.mz.app.finance.datasource.mysql.service.PermissionService;
import cn.com.mz.app.finance.module.annotation.RequiresPermissions;
import cn.com.mz.app.finance.module.annotation.RequiresRoles;
import cn.dev33.satoken.stp.StpUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 权限校验切面
 *
 * @author mz
 */
@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private PermissionService permissionService;

    /**
     * 权限校验
     */
    @Around("@annotation(requiresPermissions)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresPermissions requiresPermissions) throws Throwable {
        // 获取当前用户ID
        String userId = StpUtil.getLoginIdAsString();
        
        String[] permissions = requiresPermissions.value();
        if (permissions.length == 0) {
            return joinPoint.proceed();
        }

        // 获取用户权限
        Set<String> userPermissions = permissionService.getMenuPermission(userId);
        
        // 校验权限
        boolean hasPermission = false;
        if (requiresPermissions.logical() == RequiresPermissions.Logical.AND) {
            // AND模式：必须拥有所有权限
            hasPermission = true;
            for (String permission : permissions) {
                if (!userPermissions.contains(permission)) {
                    hasPermission = false;
                    break;
                }
            }
        } else {
            // OR模式：拥有任意一个权限即可
            for (String permission : permissions) {
                if (userPermissions.contains(permission)) {
                    hasPermission = true;
                    break;
                }
            }
        }

        if (!hasPermission) {
            throw new BusinessException("权限不足，无法访问");
        }

        return joinPoint.proceed();
    }

    /**
     * 角色校验
     */
    @Around("@annotation(requiresRoles)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequiresRoles requiresRoles) throws Throwable {
        // 获取当前用户ID
        String userId = StpUtil.getLoginIdAsString();
        
        String[] roles = requiresRoles.value();
        if (roles.length == 0) {
            return joinPoint.proceed();
        }

        // 获取用户角色
        Set<String> userRoles = permissionService.getRolePermission(userId);
        
        // 校验角色
        boolean hasRole = false;
        if (requiresRoles.logical() == RequiresRoles.Logical.AND) {
            // AND模式：必须拥有所有角色
            hasRole = true;
            for (String role : roles) {
                if (!userRoles.contains(role)) {
                    hasRole = false;
                    break;
                }
            }
        } else {
            // OR模式：拥有任意一个角色即可
            for (String role : roles) {
                if (userRoles.contains(role)) {
                    hasRole = true;
                    break;
                }
            }
        }

        if (!hasRole) {
            throw new BusinessException("角色权限不足，无法访问");
        }

        return joinPoint.proceed();
    }
}