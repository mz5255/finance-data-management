package cn.com.mz.app.finance.module.service.auth.impl;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.common.exceptions.BusinessException;
import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import cn.com.mz.app.finance.datasource.mysql.entity.user.convertor.UserConvertor;
import cn.com.mz.app.finance.datasource.mysql.service.UserService;
import cn.com.mz.app.finance.module.dto.req.UserQueryRequest;
import cn.com.mz.app.finance.module.dto.req.condition.UserIdQueryCondition;
import cn.com.mz.app.finance.module.dto.req.condition.UserPhoneAndPasswordQueryCondition;
import cn.com.mz.app.finance.module.dto.req.condition.UserPhoneQueryCondition;
import cn.com.mz.app.finance.module.service.auth.AuthService;
import cn.com.mz.app.finance.datasource.mysql.entity.user.convertor.UserInfo;
import cn.com.mz.app.finance.module.vo.UserRegisterRequest;
import cn.com.mz.app.finance.starter.lock.DistributeLock;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.module.service
 * @date 2025/9/14 22:52
 * @description: 功能描述
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserService userService;
    @Resource
    private CacheManager cacheManager;

    /**
     * 用户名布隆过滤器
     */
    private RBloomFilter<String> nickNameBloomFilter;

    /**
     * 通过用户ID对用户信息做的缓存
     */
    private Cache<String, UserDO> idUserCache;

    @PostConstruct
    public void init() {
        QuickConfig idQc = QuickConfig.newBuilder(":user:cache:id:")
                .cacheType(CacheType.BOTH)
                .expire(Duration.ofHours(2))
                .syncLocal(true)
                .build();
        idUserCache = cacheManager.getOrCreateCache(idQc);
    }

    @DistributeLock(keyExpression = "#telephone", scene = "USER_REGISTER")
    @Transactional(rollbackFor = Exception.class)
    public BaseResult<?> register(UserRegisterRequest userRegisterRequest) {
        var telephone = userRegisterRequest.getTelephone();
        String defaultNickName;
        String randomString;
        do {
            randomString = RandomUtil.randomString(6).toUpperCase();
            //前缀 + 6位随机数 + 手机号后四位
            defaultNickName = randomString + telephone.substring(7, 11);
        } while (nickNameExist(defaultNickName));

        if (StringUtils.isNotBlank(telephone)) {
            UserDO userDO = userService.getByTelephone(telephone);
            if (userDO != null) {
                throw new BusinessException("该用户已存在");
            }
        }

        UserDO user = register(telephone, defaultNickName, telephone);
        Assert.notNull(user, "用户注册失败");

        addNickName(defaultNickName);
        updateUserCache(user.getId().toString(), user);

        return BaseResult.success(user);
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
        UserInfo userInfo = UserConvertor.INSTANCE.mapToVo(user);
        response.setData(userInfo);
        return response;
    }
    /**
     * 注册
     *
     * @param telephone
     * @param nickName
     * @param password
     * @return
     */
    private UserDO register(String telephone, String nickName, String password) {

        UserDO user = new UserDO();
        user.register(telephone, nickName, password);
        return userService.save(user) ? user : null;
    }

    private boolean addNickName(String nickName) {
        if (nickName != null) {
            return this.nickNameBloomFilter != null && this.nickNameBloomFilter.add(nickName);
        }
        return true;
    }
    public boolean nickNameExist(String nickName) {
        //如果布隆过滤器中存在，再进行数据库二次判断
        if (this.nickNameBloomFilter != null && this.nickNameBloomFilter.contains(nickName)) {
            return userService.getByNikeName(nickName) != null;
        }

        return false;
    }
    private void updateUserCache(String userId, UserDO user) {
        idUserCache.put(userId, user);
    }
}
