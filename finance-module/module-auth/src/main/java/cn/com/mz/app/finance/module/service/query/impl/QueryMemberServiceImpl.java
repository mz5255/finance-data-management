package cn.com.mz.app.finance.module.service.query.impl;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.common.exceptions.BusinessException;
import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import cn.com.mz.app.finance.datasource.mysql.entity.user.convertor.UserConvertor;
import cn.com.mz.app.finance.datasource.mysql.entity.user.convertor.UserInfo;
import cn.com.mz.app.finance.datasource.mysql.service.UserService;
import cn.com.mz.app.finance.module.dto.req.QueryParam;
import cn.com.mz.app.finance.module.dto.req.UserQueryRequest;
import cn.com.mz.app.finance.module.dto.req.condition.UserIdQueryCondition;
import cn.com.mz.app.finance.module.dto.req.condition.UserPhoneAndPasswordQueryCondition;
import cn.com.mz.app.finance.module.dto.req.condition.UserPhoneQueryCondition;
import cn.com.mz.app.finance.module.dto.req.condition.UserQueryCondition;
import cn.com.mz.app.finance.module.service.query.QueryMemberService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.module.service.query
 * @date 2026/1/24 21:57
 * @description: 会员查询
 */
@Service
public class QueryMemberServiceImpl implements QueryMemberService {

    @Resource
    private UserService userService;

    public BaseResult<UserInfo> getMember(QueryParam queryParam) {
        if (StringUtils.isNotBlank(queryParam.getTelephone())){
            UserQueryRequest userQueryRequest = new UserQueryRequest(queryParam.getTelephone());
            return query(userQueryRequest);
        }
        if (queryParam.getUserId() != null){
            UserQueryRequest userQueryRequest = new UserQueryRequest(queryParam.getUserId());
            return query(userQueryRequest);
        }
        return BaseResult.success();
    }


    public BaseResult<UserInfo> query(UserQueryRequest userQueryRequest) {
        //使用switch表达式精简代码，如果这里编译不过，参考我的文档调整IDEA的JDK版本
        UserDO user = switch (userQueryRequest.getUserQueryCondition()) {
            case UserIdQueryCondition userIdQueryCondition:
                yield userService.getById(userIdQueryCondition.getUserId());
            case UserPhoneQueryCondition userPhoneQueryCondition:
                yield userService.getByTelephone(userPhoneQueryCondition.getTelephone());
            case UserPhoneAndPasswordQueryCondition userPhoneAndPasswordQueryCondition:
                yield userService.getByPhoneAndPass(userPhoneAndPasswordQueryCondition.getTelephone(), userPhoneAndPasswordQueryCondition.getPassword());
            default:
                throw new BusinessException("该查询方式为被定义: " + userQueryRequest.getUserQueryCondition().getClass().getName() + "，请检查");
        };

        BaseResult<UserInfo> response = new BaseResult();
        response.setCode(200);
        UserInfo userInfo = UserConvertor.INSTANCE.toUserInfo(user);
        response.setData(userInfo);
        return response;
    }
}
