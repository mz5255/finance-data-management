package cn.com.mz.app.finance.datasource.mysql.entity.user.enums;

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
}
