package cn.com.mz.app.finance.module.service.auth.impl;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.common.exceptions.BusinessException;
import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import cn.com.mz.app.finance.datasource.mysql.entity.user.convertor.UserConvertor;
import cn.com.mz.app.finance.datasource.mysql.entity.user.convertor.UserInfo;
import cn.com.mz.app.finance.datasource.mysql.service.UserService;
import cn.com.mz.app.finance.module.dto.req.LoginParam;
import cn.com.mz.app.finance.module.dto.req.UserQueryRequest;
import cn.com.mz.app.finance.module.dto.req.condition.UserIdQueryCondition;
import cn.com.mz.app.finance.module.dto.req.condition.UserPhoneAndPasswordQueryCondition;
import cn.com.mz.app.finance.module.dto.req.condition.UserPhoneQueryCondition;
import cn.com.mz.app.finance.module.service.auth.AuthService;
import cn.com.mz.app.finance.module.vo.LoginReq;
import cn.com.mz.app.finance.module.vo.UserRegisterRequest;
import cn.com.mz.app.finance.starter.lock.DistributeLock;
import cn.com.mz.app.finance.starter.utils.RedisUtils;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static cn.com.mz.app.finance.starter.constant.CacheConstant.CAPTCHA_KEY_PREFIX;

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
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse response;
    @Resource

    private RedisUtils redisUtils;

    private static final String ROOT_CAPTCHA = "5255";
    /**
     * 默认登录超时时间：7天
     */
    private static final Integer DEFAULT_LOGIN_SESSION_TIMEOUT = 60 * 60 * 24 * 7;
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
        updateUserCache(user.getId(), user);

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

    @Override
    public void captchaImage(String telephone) {
        try {
            // 设置响应头
            response.setHeader("Cache-Control", "no-store, no-cache");
            response.setContentType("image/jpeg");

            // 生成验证码文本
            String captchaText = generateCaptcha();

            // 生成图片验证码
            BufferedImage image = createCaptchaImage(captchaText);

            // 将验证码存储到session或Redis中
            String sessionId = request.getSession().getId();
            redisUtils.set(
                    CAPTCHA_KEY_PREFIX + sessionId,
                    captchaText,
                    5, TimeUnit.MINUTES
            );

            // 输出图片
            javax.imageio.ImageIO.write(image, "JPEG", response.getOutputStream());
        } catch (Exception e) {
            throw new BusinessException("验证码生成失败");
        }
    }

    /**
     * 生成6位随机验证码
     */
    private String generateCaptcha() {
        StringBuilder captcha = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 6; i++) {
            captcha.append(random.nextInt(10));
        }
        return captcha.toString();
    }
    /**
     * 创建验证码图片
     */
    private BufferedImage createCaptchaImage(String captchaText) {
        int width = 120;
        int height = 40;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics graphics = bufferedImage.getGraphics();

        // 设置背景色
        graphics.setColor(java.awt.Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        // 绘制干扰线
        java.util.Random random = new java.util.Random();
        graphics.setColor(java.awt.Color.GRAY);
        for (int i = 0; i < 10; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            graphics.drawLine(x1, y1, x2, y2);
        }

        // 绘制验证码字符
        graphics.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        graphics.setColor(java.awt.Color.BLACK);
        for (int i = 0; i < captchaText.length(); i++) {
            char c = captchaText.charAt(i);
            graphics.drawString(String.valueOf(c), 20 + i * 15, 25);
        }

        graphics.dispose();
        return bufferedImage;
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

    /**
     * 登录
     * @param loginParam
     * @return
     */
    @Transactional
    public BaseResult<LoginReq> login(LoginParam loginParam) {
        //验证码校验
        String cachedCode = redisUtils.get(CAPTCHA_KEY_PREFIX + loginParam.getTelephone());
        if (!StringUtils.equalsIgnoreCase(cachedCode, loginParam.getCaptcha())) {
            throw new BusinessException("验证码错误");
        }

        //判断是注册还是登陆
        //查询用户信息
        UserQueryRequest userQueryRequest = new UserQueryRequest(loginParam.getTelephone());
        BaseResult<UserInfo> userQueryResponse = query(userQueryRequest);
        UserInfo userInfo = userQueryResponse.getData();
        if (userInfo == null) {
            //需要注册
            UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
            userRegisterRequest.setTelephone(loginParam.getTelephone());

            BaseResult<?> response = register(userRegisterRequest);
            if (response.isSuccess()) {
                userQueryResponse = query(userQueryRequest);
                userInfo = userQueryResponse.getData();
                LoginReq loginVO = getLoginReq(loginParam, userInfo);
                return BaseResult.success(loginVO);
            }
            return BaseResult.error(response.getCode(), response.getMessage());
        } else {
            //登录
            LoginReq loginVO = getLoginReq(loginParam, userInfo);
            return BaseResult.success(loginVO);
        }
    }

    /**
     *  填充用户信息至上下文中 & 更新用户最后一次登录时间
     * @param loginParam
     * @param userInfo
     * @return
     */
    @Transactional
    public LoginReq getLoginReq(LoginParam loginParam, UserInfo userInfo) {
        StpUtil.login(userInfo.getUserId(), new SaLoginModel().setIsLastingCookie(loginParam.getRememberMe())
                .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
        StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
        LoginReq loginVO = new LoginReq(userInfo);
        UserDO userDO = UserConvertor.INSTANCE.mapToEntity(userInfo);
        userDO.login();
        userService.updateById(userDO);
        return loginVO;
    }
}
