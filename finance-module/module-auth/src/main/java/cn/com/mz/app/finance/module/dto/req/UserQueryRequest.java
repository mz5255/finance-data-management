package cn.com.mz.app.finance.module.dto.req;

import cn.com.mz.app.finance.module.dto.req.condition.UserIdQueryCondition;
import cn.com.mz.app.finance.module.dto.req.condition.UserPhoneAndPasswordQueryCondition;
import cn.com.mz.app.finance.module.dto.req.condition.UserPhoneQueryCondition;
import cn.com.mz.app.finance.module.dto.req.condition.UserQueryCondition;
import lombok.Data;

import java.io.Serializable;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.module.dto.req
 * @date 2025/9/20 22:08
 * @description: 功能描述
 */
@Data
public class UserQueryRequest implements Serializable {
    private UserQueryCondition userQueryCondition;

    public UserQueryRequest(Long userId) {
        UserIdQueryCondition userIdQueryCondition = new UserIdQueryCondition();
        userIdQueryCondition.setUserId(userId);
        this.userQueryCondition = userIdQueryCondition;
    }

    public UserQueryRequest(String telephone) {
        UserPhoneQueryCondition userPhoneQueryCondition = new UserPhoneQueryCondition();
        userPhoneQueryCondition.setTelephone(telephone);
        this.userQueryCondition = userPhoneQueryCondition;
    }

    public UserQueryRequest(String telephone, String password) {
        UserPhoneAndPasswordQueryCondition userPhoneAndPasswordQueryCondition = new UserPhoneAndPasswordQueryCondition();
        userPhoneAndPasswordQueryCondition.setTelephone(telephone);
        userPhoneAndPasswordQueryCondition.setPassword(password);
        this.userQueryCondition = userPhoneAndPasswordQueryCondition;
    }
}
