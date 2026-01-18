package cn.com.mz.app.finance.application.webapi.demo;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import cn.com.mz.app.finance.module.annotation.RequiresPermissions;
import cn.com.mz.app.finance.module.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.application.webapi.demo
 * @date 2026/1/18 22:27
 * @description: demo
 */
public class DemoTest {
    @RequiresPermissions("system:user:list")
    @GetMapping("/list")
    public BaseResult<List<UserDO>> getUserList() {
        // 需要system:user:list权限才能访问
        return BaseResult.success();
    }

    @RequiresRoles(value = {"admin", "manager"}, logical = RequiresRoles.Logical.OR)
    @PostMapping("/delete")
    public BaseResult<Boolean> deleteUser() {
        // 需要admin或manager角色才能访问
        return BaseResult.success();
    }
}
