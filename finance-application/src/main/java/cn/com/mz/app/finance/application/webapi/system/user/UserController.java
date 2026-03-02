package cn.com.mz.app.finance.application.webapi.system.user;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.datasource.mysql.entity.user.dto.UserDTO;
import cn.com.mz.app.finance.datasource.mysql.service.UserService;
import cn.com.mz.app.finance.module.dto.req.QueryParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.application.webapi.system.user
 * @date 2026/3/1 18:55
 * @description: 功能描述
 */
@RestController
@RequestMapping("/api/finance-data/user")
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {

    @Resource
    private UserService userService;
//    @RequiresRoles(value = {"admin"}, logical = RequiresRoles.Logical.OR)
    @PostMapping("/page")
    public BaseResult<IPage<UserDTO>> getUserList(@RequestBody QueryParam queryParam) {
        IPage<UserDTO> membersByPage = userService.getMembersByPage(queryParam.getPageNum(),queryParam.getPageSize());
        return BaseResult.success(membersByPage);
    }
}
