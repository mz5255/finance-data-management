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
 * @author Hollis
 */
@Getter
@Setter
@NoArgsConstructor
public class UserInfo extends BasicUserInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    @SensitiveStrategyPhone
    private String telephone;

    /**
     * 状态
     *
     * @see UserStateEnum
     */
    private String state;

    /**
     * 实名认证
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

    public boolean userCanBuy() {

        if (this.getUserRole() != null && !this.getUserRole().equals(UserRole.CUSTOMER)) {
            return false;
        }
        //是否实名认证
        if (this.getState() != null && !this.getCertification()) {
            return false;
        }
        return true;
    }
}
