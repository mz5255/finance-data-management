package cn.com.mz.app.finance.datasource.mysql.entity.user.dto;

import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import cn.com.mz.app.finance.datasource.mysql.entity.user.enums.UserRole;
import cn.com.mz.app.finance.datasource.mysql.entity.user.enums.UserStateEnum;
import cn.com.mz.app.finance.datasource.mysql.entity.user.base.BasicUserInfo;
import com.github.houbb.sensitive.annotation.strategy.SensitiveStrategyPhone;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author mz
 */
@Getter
@Setter
@NoArgsConstructor
public class UserDTO extends BasicUserInfo {

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
    private LocalDateTime createTime;

    /**
     * 密码盐
     */
    private String salt;

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

    public static UserDO build(UserDTO userDTO) {
        UserDO userDO = new UserDO();
        userDO.setId(userDTO.getUserId());
        userDO.setNickName(userDTO.getNickName());
        userDO.setTelephone(userDTO.getTelephone());
        userDO.setState(UserStateEnum.valueOf(userDTO.getState()));
        userDO.setCertification(userDTO.getCertification());
        userDO.setUserRole(userDTO.getUserRole());
        userDO.setProfilePhotoUrl(userDTO.getProfilePhotoUrl());
        userDO.setSalt(userDTO.getSalt());
        return userDO;
    }
    
    public static UserDTO build(UserDO userDO) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userDO.getId());
        userDTO.setNickName(userDO.getNickName());
        userDTO.setTelephone(userDO.getTelephone());
        userDTO.setState(userDO.getState().name());
        userDTO.setCertification(userDO.getCertification());
        userDTO.setUserRole(userDO.getUserRole());
        userDTO.setProfilePhotoUrl(userDO.getProfilePhotoUrl());
        userDTO.setSalt(userDO.getSalt());
        return userDTO;
    }

    public static List<UserDTO> build(List<UserDO> userDOList) {
        return userDOList.stream().map(UserDTO::build).toList();
    }
}
