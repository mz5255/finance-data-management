package cn.com.mz.app.finance.module.vo;

import cn.com.mz.app.finance.datasource.mysql.entity.user.convertor.UserInfo;
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
     * 用户标识，如用户ID
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


    public LoginReq(UserInfo userInfo) {
        this.userId = userInfo.getUserId().toString();
        this.token = StpUtil.getTokenValue();
        this.tokenExpiration = StpUtil.getTokenSessionTimeout();
    }
}
