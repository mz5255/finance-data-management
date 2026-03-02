package cn.com.mz.app.finance.datasource.mysql.entity.user.enums;

import cn.com.mz.app.finance.common.exceptions.BusinessException;

/**
 * 用户状态
 *
 * @author hollis
 */
public enum UserStateEnum {
    /**
     * 创建成功
     */
    INIT,
    /**
     * 实名认证
     */
    AUTH,

    /**
     * 冻结
     */
    FROZEN;

    public static UserStateEnum getByValue(String value) {
        for (UserStateEnum state : values()) {
            if (state.name().equals(value)) {
                return state;
            }
        }
        throw new BusinessException("用户状态不存在");
    }
}
