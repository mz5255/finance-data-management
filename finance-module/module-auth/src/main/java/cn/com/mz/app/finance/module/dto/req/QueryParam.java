package cn.com.mz.app.finance.module.dto.req;

import lombok.Data;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.module.dto.req
 * @date 2026/1/24 21:55
 * @description: 功能描述
 */
@Data
public class QueryParam {
    private String telephone;

    private Long userId;

    private Integer pageNum;

    private Integer pageSize;
}
