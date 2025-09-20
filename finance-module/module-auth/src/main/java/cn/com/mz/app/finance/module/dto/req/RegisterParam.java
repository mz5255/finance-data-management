package cn.com.mz.app.finance.module.dto.req;

import cn.com.mz.app.finance.common.aspect.mobile.IsMobile;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Hollis
 */
@Setter
@Getter
public class RegisterParam {

    /**
     * 手机号
     */
    @IsMobile
    private String telephone;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String captcha;

}
