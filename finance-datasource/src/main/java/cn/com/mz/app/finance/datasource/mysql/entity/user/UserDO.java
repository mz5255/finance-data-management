package cn.com.mz.app.finance.datasource.mysql.entity.user;

import cn.com.mz.app.finance.common.exceptions.BusinessException;
import cn.com.mz.app.finance.datasource.mysql.entity.base.BaseEntity;
import cn.com.mz.app.finance.datasource.mysql.entity.handler.AesEncryptTypeHandler;
import cn.com.mz.app.finance.datasource.mysql.entity.user.enums.UserStateEnum;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.houbb.sensitive.annotation.strategy.SensitiveStrategyPhone;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.UUID;

/**
 * 用户
 *
 * @author mz
 */
@Getter
@Setter
@TableName("Users")
public class UserDO extends BaseEntity {
    /**
     * 昵称
     */
    private String nickName;

    /**
     * 密码
     */
    private String passwordHash;

    /**
     * 状态
     */
    private UserStateEnum state;

    /**
     * 手机号
     */
    @SensitiveStrategyPhone
    private String telephone;

    /**
     * 最后登录时间
     */
    private LocalTime lastLoginTime;

    /**
     * 头像地址
     */
    private String profilePhotoUrl;

    /**
     * 区块链地址
     */
    private String blockChainUrl;

    /**
     * 区块链平台
     */
    private String blockChainPlatform;

    /**
     * 实名认证
     */
    private Boolean certification;

    /**
     * 真实姓名
     */
    @TableField(typeHandler = AesEncryptTypeHandler.class)
    private String realName;

    /**
     * 身份证hash
     */
    @TableField(typeHandler = AesEncryptTypeHandler.class)
    private String idCardNo;

    /**
     * 密码盐
     */
    private String salt;

    /**
     * 用户角色
     */
    private String userRole;

    public static String getPassword(String password, String salt) {
        return DigestUtil.md5Hex(password + salt, StandardCharsets.UTF_8);
    }

    public UserDO register(Long userId, String telephone, String password) {
        this.setId(userId);
        this.telephone = telephone;
        this.nickName = nickName;
        this.salt = UUID.randomUUID().toString().substring(0, 4);
        this.passwordHash = getPassword(password, salt);
        this.state = UserStateEnum.INIT;
        this.userRole = "customer";
        return this;
    }

    public UserDO auth(String realName, String idCard) {
        this.realName = realName;
        this.idCardNo = idCard;
        this.certification = true;
        this.state = UserStateEnum.AUTH;
        return this;
    }

    public UserDO active(String blockChainUrl, String blockChainPlatform) {
        this.blockChainPlatform = blockChainPlatform;
        this.blockChainUrl = blockChainUrl;
        return this;
    }

    public UserDO registerAdmin(String telephone, String nickName, String password) {
        this.telephone = telephone;
        this.nickName = nickName;
        this.passwordHash = getPassword(password, salt);
        this.userRole = "admin";
        return this;
    }

    public void login(String password) {
        this.lastLoginTime = LocalTime.now();
        password = getPassword(password, salt);
        if (!password.equals(this.passwordHash)) {
            throw new BusinessException("密码错误");
        }
    }
    public boolean canModifyInfo() {
        return state == UserStateEnum.INIT || state == UserStateEnum.AUTH;
    }
}
