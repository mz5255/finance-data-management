package cn.com.mz.app.finance.module.vo;

import cn.com.mz.app.finance.datasource.mysql.entity.user.dto.UserDTO;
import cn.dev33.satoken.stp.StpUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Hollis
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoginReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户标识，如用户ID（使用String类型避免JavaScript精度丢失问题）
     */
    private String userId;
    /**
     * 访问令牌
     */
    private String token;

    /**
     * 令牌过期时间
     */
    private Long tokenExpiration;


    public LoginReq(UserDTO userDTO) {
        // 将Long类型的userId转换为String，避免JavaScript精度丢失
        this.userId = userDTO.getUserId().toString();
        this.token = StpUtil.getTokenValue();
        this.tokenExpiration = StpUtil.getTokenSessionTimeout();
    }
}
