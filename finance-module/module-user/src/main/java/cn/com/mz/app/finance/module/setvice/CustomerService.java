package cn.com.mz.app.finance.module.setvice;

import cn.com.mz.app.finance.datasource.mysql.entity.user.dto.UserDTO;
import cn.com.mz.app.finance.datasource.mysql.entity.user.enums.UserStateEnum;
import cn.com.mz.app.finance.datasource.mysql.service.UserService;
import cn.com.mz.app.finance.module.dto.req.QueryParam;
import cn.com.mz.app.finance.module.dto.req.UpdateParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.module.setvice
 * @date 2026/3/13 23:31
 * @description: 功能描述
 */
@Service
public class CustomerService {

    @Resource
    private UserService userService;

    public IPage<UserDTO> getMembersByPage(QueryParam queryParam) {
        return userService.getMembersByPage(queryParam.getPageNum(), queryParam.getPageSize());
    }

    public IPage<UserDTO> getAllUsersByPage(QueryParam queryParam) {
        return userService.getAllUsersByPage(queryParam.getPageNum(), queryParam.getPageSize());
    }

    public boolean updateStateById(Long userId, UserStateEnum stateEnum) {
        return userService.updateStateById(userId, stateEnum);
    }

    public boolean deleteById(Long userId) {
        return userService.deleteById(userId);
    }

    public boolean updateProfile(UpdateParam updateParam) {
        return userService.updateProfile(updateParam.getUserId(), updateParam.getNickName(), updateParam.getTelephone());
    }
}
