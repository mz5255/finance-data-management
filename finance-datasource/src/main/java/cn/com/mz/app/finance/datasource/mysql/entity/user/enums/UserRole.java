package cn.com.mz.app.finance.datasource.mysql.entity.user.enums;

/**
 * @author Hollis
 */
public enum UserRole {

    /**
     * 普通用户
     */
    CUSTOMER,

    /**
     * 超级管理员
     */
    SUPERADMIN,

    /**
     * 管理员
     */
    ADMIN;

    public static UserRole getByValue(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.name().equals(value)) {
                return role;
            }
        }
        return null;
    }
}
