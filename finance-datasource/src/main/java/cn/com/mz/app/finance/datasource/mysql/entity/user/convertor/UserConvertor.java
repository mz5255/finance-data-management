package cn.com.mz.app.finance.datasource.mysql.entity.user.convertor;

import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import cn.com.mz.app.finance.datasource.mysql.entity.user.base.BasicUserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户对象转换器
 * <p>
 * 负责用户实体（UserDO）与 DTO（UserInfo/BasicUserInfo）之间的相互转换
 *
 * @author Hollis
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserConvertor {

    UserConvertor INSTANCE = Mappers.getMapper(UserConvertor.class);

    /**
     * 转换为完整的用户信息 VO
     *
     * @param userDO 用户实体对象
     * @return 用户信息 VO
     */
    @Mapping(target = "userId", source = "id", qualifiedByName = "longToString")
    UserInfo toUserInfo(UserDO userDO);

    /**
     * 转换为基础用户信息 VO（仅包含基本信息字段）
     *
     * @param userDO 用户实体对象
     * @return 基础用户信息 VO
     */
    @Mapping(target = "userId", source = "id", qualifiedByName = "longToString")
    BasicUserInfo toBasicUserInfo(UserDO userDO);

    /**
     * 转换为用户实体对象
     *
     * @param userInfo 用户信息 VO
     * @return 用户实体对象
     */
    @Mapping(target = "id", source = "userId", qualifiedByName = "stringToLong")
    UserDO toUserDO(UserInfo userInfo);

    /**
     * 批量转换为用户信息 VO
     *
     * @param userDOList 用户实体对象列表
     * @return 用户信息 VO 列表
     */
    List<UserInfo> toUserInfoList(List<UserDO> userDOList);

    /**
     * Long 类型转换为 String
     * <p>
     * 用于数据库 Long 类型主键与前端 String 类型 ID 的转换
     *
     * @param value Long 类型的值
     * @return String 类型的值，如果输入为 null 则返回 null
     */
    @Named("longToString")
    default String longToString(Long value) {
        return value == null ? null : value.toString();
    }

    /**
     * String 类型转换为 Long
     * <p>
     * 用于前端 String 类型 ID 与数据库 Long 类型主键的转换
     *
     * @param value String 类型的值
     * @return Long 类型的值，如果输入为 null 或空字符串则返回 null
     * @throws NumberFormatException 当输入的字符串不是有效的数字格式时抛出
     */
    @Named("stringToLong")
    default Long stringToLong(String value) {
        return value == null || value.isEmpty() ? null : Long.parseLong(value);
    }
}
