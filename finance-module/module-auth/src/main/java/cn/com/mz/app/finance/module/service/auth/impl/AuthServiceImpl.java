package cn.com.mz.app.finance.module.service.auth.impl;

import cn.com.mz.app.finance.module.service.auth.UserOperatorResponse;
import cn.com.mz.app.finance.starter.lock.DistributeLock;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.module.service
 * @date 2025/9/14 22:52
 * @description: 功能描述
 */
public class AuthServiceImpl {

    /**
     * 用户名布隆过滤器
     */
    private RBloomFilter<String> nickNameBloomFilter;

//    @DistributeLock(keyExpression = "#telephone", scene = "USER_REGISTER")
//    @Transactional(rollbackFor = Exception.class)
//    public UserOperatorResponse register(String telephone, String inviteCode) {
//        String defaultNickName;
//        String randomString;
//        do {
//            randomString = RandomUtil.randomString(6).toUpperCase();
//            //前缀 + 6位随机数 + 手机号后四位
//            defaultNickName = randomString + telephone.substring(7, 11);
//        } while (nickNameExist(defaultNickName) || inviteCodeExist(randomString));
//
//        String inviterId = null;
//        if (StringUtils.isNotBlank(inviteCode)) {
//            User inviter = userMapper.findByInviteCode(inviteCode);
//            if (inviter != null) {
//                inviterId = inviter.getId().toString();
//            }
//        }
//
//        User user = register(telephone, defaultNickName, telephone, randomString, inviterId);
//        Assert.notNull(user, UserErrorCode.USER_OPERATE_FAILED.getCode());
//
//        addNickName(defaultNickName);
//        addInviteCode(randomString);
//        updateInviteRank(inviterId);
//        updateUserCache(user.getId().toString(), user);
//
//        //加入流水
//        long streamResult = userOperateStreamService.insertStream(user, UserOperateTypeEnum.REGISTER);
//        Assert.notNull(streamResult, () -> new BizException(RepoErrorCode.UPDATE_FAILED));
//
//        UserOperatorResponse userOperatorResponse = new UserOperatorResponse();
//        userOperatorResponse.setSuccess(true);
//
//        return userOperatorResponse;
//    }
//
//    public boolean nickNameExist(String nickName) {
//        //如果布隆过滤器中存在，再进行数据库二次判断
//        if (this.nickNameBloomFilter != null && this.nickNameBloomFilter.contains(nickName)) {
//            return userMapper.findByNickname(nickName) != null;
//        }
//
//        return false;
//    }
}
