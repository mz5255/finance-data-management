package cn.com.mz.app.finance.module.dto.req;

import lombok.Data;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.module.dto.req
 * @date 2026/3/13 23:28
 * @description: 功能描述
 */
@Data
public class UpdateParam {
    private Long userId;
    private String nickName;
    private String telephone;
    private String oldPassword;
    private String newPassword;
}
