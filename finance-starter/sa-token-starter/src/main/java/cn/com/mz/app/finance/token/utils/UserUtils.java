package cn.com.mz.app.finance.token.utils;

import cn.com.mz.app.finance.common.exceptions.BusinessException;
import cn.dev33.satoken.stp.StpUtil;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.token.utils
 * @date 2026/3/1 15:02
 * @description: 功能描述
 */
public class UserUtils {
    public Long getUserId() {
        if (StpUtil.isLogin()) {
            return StpUtil.getSession().getLong("userId");
        } else {
            throw new BusinessException("请先登录再来");
        }
    }
}
