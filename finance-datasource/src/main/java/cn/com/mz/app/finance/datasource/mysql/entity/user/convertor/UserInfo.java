package cn.com.mz.app.finance.datasource.mysql.entity.user.convertor;

import cn.com.mz.app.finance.datasource.mysql.entity.user.enums.UserRole;
import cn.com.mz.app.finance.datasource.mysql.entity.user.enums.UserStateEnum;
import cn.com.mz.app.finance.datasource.mysql.entity.user.base.BasicUserInfo;
import com.github.houbb.sensitive.annotation.strategy.SensitiveStrategyPhone;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 用户信息 VO
 * <p>
 * 完整的用户信息展示对象，继承自 BasicUserInfo，包含更多用户详细信息
 *
 * @author Hollis
 */
@Getter
@Setter
@NoArgsConstructor
public class UserInfo extends BasicUserInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 手机号（脱敏）
     */
    @SensitiveStrategyPhone
    private String telephone;

    /**
     * 用户状态
     *
     * @see UserStateEnum
     */
    private String state;

    /**
     * 是否已实名认证
     */
    private Boolean certification;

    /**
     * 用户角色
     */
    private UserRole userRole;

    /**
     * 注册时间
     */
    private Date createTime;

    /**
     * 判断用户是否具有购买资格
     * <p>
     * 购买资格判断标准：
     * <ul>
     *   <li>用户角色必须是 CUSTOMER（客户）</li>
     *   <li>必须完成实名认证</li>
     * </ul>
     *
     * @return true 如果用户具有购买资格，否则返回 false
     */
    public boolean userCanBuy() {
        boolean isCustomer = getUserRole() == UserRole.CUSTOMER;
        boolean isCertified = getCertification() != null && getCertification();
        return isCustomer && isCertified;
    }
}
