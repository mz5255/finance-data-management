package cn.com.mz.app.finance.module.service.query;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.datasource.mysql.entity.user.dto.UserDTO;
import cn.com.mz.app.finance.module.dto.req.QueryParam;
import cn.com.mz.app.finance.module.dto.req.UserQueryRequest;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.module.service.query.impl
 * @date 2026/1/24 22:02
 * @description: 功能描述
 */
public interface QueryMemberService {
    BaseResult<UserDTO> query(UserQueryRequest userQueryRequest);

    BaseResult<UserDTO> getMember(QueryParam queryParam);
}
