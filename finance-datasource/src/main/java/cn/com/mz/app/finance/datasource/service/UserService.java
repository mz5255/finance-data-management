package cn.com.mz.app.finance.datasource.service;

import cn.com.mz.app.finance.datasource.entity.user.UserDO;
import cn.com.mz.app.finance.datasource.entity.user.enums.UserRole;
import cn.com.mz.app.finance.datasource.entity.user.enums.UserStateEnum;
import cn.com.mz.app.finance.datasource.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户服务实现类
 *
 * @author mz
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据手机号查询用户
     *
     * @param telephone 手机号
     * @return 用户信息
     */
    public UserDO getByTelephone(String telephone) {
        return userMapper.selectOne(new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getTelephone, telephone));
    }

    /**
     * 根据邀请码查询用户
     *
     * @param inviteCode 邀请码
     * @return 用户信息
     */
    public UserDO getByInviteCode(String inviteCode) {
        return userMapper.selectOne(new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getInviteCode, inviteCode));
    }

    /**
     * 根据用户状态查询用户列表
     *
     * @param state 用户状态
     * @return 用户列表
     */
    public List<UserDO> getByState(UserStateEnum state) {
        return userMapper.selectList(new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getState, state));
    }

    /**
     * 根据用户角色查询用户列表
     *
     * @param userRole 用户角色
     * @return 用户列表
     */
    public List<UserDO> getByUserRole(UserRole userRole) {
        return userMapper.selectList(new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getUserRole, userRole));
    }

    /**
     * 根据邀请人ID查询被邀请用户列表
     *
     * @param inviterId 邀请人ID
     * @return 被邀请用户列表
     */
    public List<UserDO> getByInviterId(String inviterId) {
        return userMapper.selectList(new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getInviterId, inviterId));
    }

    /**
     * 根据用户ID更新用户状态
     *
     * @param id 用户ID
     * @param state 新状态
     * @return 是否更新成功
     */
    public boolean updateStateById(Long id, UserStateEnum state) {
        return userMapper.update(null, new LambdaUpdateWrapper<UserDO>()
                .eq(UserDO::getId, id)
                .set(UserDO::getState, state)) > 0;
    }

    /**
     * 更新用户最后登录时间
     *
     * @param id 用户ID
     * @return 是否更新成功
     */
    public boolean updateLastLoginTime(Long id) {
        return userMapper.update(null, new LambdaUpdateWrapper<UserDO>()
                .eq(UserDO::getId, id)
                .set(UserDO::getLastLoginTime, new Date())) > 0;
    }

    /**
     * 统计用户总数
     *
     * @return 用户总数
     */
    public Long countUsers() {
        return userMapper.selectCount(new LambdaQueryWrapper<UserDO>());
    }

    /**
     * 统计已认证用户数
     *
     * @return 已认证用户数
     */
    public Long countCertifiedUsers() {
        return userMapper.selectCount(new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getCertification, true));
    }

    /**
     * 保存用户
     *
     * @param user 用户信息
     * @return 是否保存成功
     */
    public boolean save(UserDO user) {
        return userMapper.insert(user) > 0;
    }

    /**
     * 根据ID更新用户信息
     *
     * @param user 用户信息
     * @return 是否更新成功
     */
    public boolean updateById(UserDO user) {
        return userMapper.updateById(user) > 0;
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public UserDO getById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    public boolean deleteById(Long id) {
        return userMapper.deleteById(id) > 0;
    }
}