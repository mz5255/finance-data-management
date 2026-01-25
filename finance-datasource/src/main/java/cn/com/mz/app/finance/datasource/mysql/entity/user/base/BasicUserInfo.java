package cn.com.mz.app.finance.datasource.mysql.entity.user.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 基础用户信息 VO
 * <p>
 * 仅包含用户的基本展示信息，用于对外接口返回，避免暴露敏感数据
 *
 * @author Hollis
 */
@Getter
@Setter
@NoArgsConstructor
public class BasicUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    private String userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 头像地址
     */
    private String profilePhotoUrl;
}
