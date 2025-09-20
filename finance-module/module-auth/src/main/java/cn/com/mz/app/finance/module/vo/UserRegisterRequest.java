package cn.com.mz.app.finance.module.vo;

import cn.com.mz.app.finance.common.dto.base.BaseReq;
import lombok.*;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.module.vo
 * @date 2025/9/14 22:27
 * @description: 功能描述
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest extends BaseReq {

    /**
     * 手机号
     */
    private String telephone;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 密码
     */
    private String password;

}