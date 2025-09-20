package cn.com.mz.app.finance.datasource.mysql.service;

import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import cn.com.mz.app.finance.datasource.mysql.entity.user.enums.UserRole;
import cn.com.mz.app.finance.datasource.mysql.entity.user.enums.UserStateEnum;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author mz
 */
public interface UserService {

    /**
     * 根据手机号查询用户
     *
     * @param telephone 手机号
     * @return 用户信息
     */
    UserDO getByTelephone(String telephone);

    /**
     * 根据用户名
     *
     * @param nikeName 手机号
     * @return 用户信息
     */
    UserDO getByNikeName(String nikeName);

    /**
     * 根据用户状态查询用户列表
     *
     * @param state 用户状态
     * @return 用户列表
     */
    List<UserDO> getByState(UserStateEnum state);

    /**
     * 根据用户角色查询用户列表
     *
     * @param userRole 用户角色
     * @return 用户列表
     */
    List<UserDO> getByUserRole(UserRole userRole);

    /**
     * 根据邀请人ID查询被邀请用户列表
     *
     * @param phone 邀请人ID
     * @return 被邀请用户列表
     */
    UserDO getByPhoneAndPass(String phone,String password);

    /**
     * 根据用户ID更新用户状态
     *
     * @param id 用户ID
     * @param state 新状态
     * @return 是否更新成功
     */
    boolean updateStateById(Long id, UserStateEnum state);

    /**
     * 更新用户最后登录时间
     *
     * @param id 用户ID
     * @return 是否更新成功
     */
    boolean updateLastLoginTime(Long id);

    /**
     * 统计用户总数
     *
     * @return 用户总数
     */
    Long countUsers();

    /**
     * 统计已认证用户数
     *
     * @return 已认证用户数
     */
    Long countCertifiedUsers();

    /**
     * 保存用户
     *
     * @param user 用户信息
     * @return 是否保存成功
     */
    boolean save(UserDO user);

    /**
     * 根据ID更新用户信息
     *
     * @param user 用户信息
     * @return 是否更新成功
     */
    boolean updateById(UserDO user);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserDO getById(Long id);

    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);
}