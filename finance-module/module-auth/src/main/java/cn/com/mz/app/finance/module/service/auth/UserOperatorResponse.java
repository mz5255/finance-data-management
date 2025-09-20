package cn.com.mz.app.finance.module.service.auth;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserOperatorResponse extends BaseResult {

    private UserDO user;
}
