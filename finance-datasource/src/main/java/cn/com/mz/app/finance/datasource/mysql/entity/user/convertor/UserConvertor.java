package cn.com.mz.app.finance.datasource.mysql.entity.user.convertor;


import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import cn.com.mz.app.finance.datasource.mysql.entity.user.base.BasicUserInfo;
import cn.com.mz.app.finance.datasource.mysql.entity.user.convertor.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author Hollis
 */
@Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserConvertor {

    UserConvertor INSTANCE = Mappers.getMapper(UserConvertor.class);

    /**
     * 转换为vo
     *
     * @param request
     * @return
     */
    @Mapping(target = "userId", source = "request.id")
    @Mapping(target = "createTime", source = "request.createTime")
    public UserInfo mapToVo(UserDO request);

    /**
     * 转换为简单的VO
     * @param request
     * @return
     */
    @Mapping(target = "userId", source = "request.id")
    public BasicUserInfo mapToBasicVo(UserDO request);

    /**
     * 转换为实体
     *
     * @param request
     * @return
     */
    @Mapping(target = "id", source = "request.userId")
    public UserDO mapToEntity(UserInfo request);

    /**
     * 转换为VO
     *
     * @param request
     * @return
     */
    @Mapping(target = "userId", source = "request.id")
    public List<UserInfo> mapToVo(List<UserDO> request);
}
