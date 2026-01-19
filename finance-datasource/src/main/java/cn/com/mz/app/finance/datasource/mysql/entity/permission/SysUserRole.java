package cn.com.mz.app.finance.datasource.mysql.entity.permission;

import cn.com.mz.app.finance.datasource.mysql.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 *@author 马震
 *@date 2026/1/19 11:42
 *@version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
public class SysUserRole extends BaseEntity {
    private String userId;
    private Long roleId;
}
