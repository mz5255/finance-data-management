package cn.com.mz.app.finance.module.web_spider.utils;

import cn.com.mz.app.finance.common.utils.HttpUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自动登录爬虫工具类
 * 提供登录认证和带认证的请求发送功能
 *
 * @author mz
 * @date 2026/3/16 23:10
 */
public class AutoLoginSpiderUtil {

    private static final Logger logger = LoggerFactory.getLogger(AutoLoginSpiderUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 存储登录后的 Cookie 和 Token
    private String loginCookie = null;
    private String accessToken = null;

    public static void main(String[] args) {
        AutoLoginSpiderUtil spider = new AutoLoginSpiderUtil();

        String phone = "15639224316";
        String password = "mazhen5255";

        // 1. 登录
        if (!spider.login(phone, password)) {
            logger.error("登录失败");
            return;
        }

        logger.info("登录成功，Token: {}", spider.getAccessToken());

        // 2. 示例：获取数据
        String url = "https://ydtwx.cloud.haizhikao.com/api/manage-app/app/course/111244/catalog?categoryId=13040";
        String response = spider.sendAuthenticatedRequest(url);
        if (response != null) {
            logger.info("获取数据成功: {}", response);
        }
    }

    /**
     * 登录
     *
     * @param phone    手机号
     * @param password 密码
     * @return 登录是否成功
     */
    public boolean login(String phone, String password) {
        try {
            logger.info("正在登录...");
            logger.info("账号: {}", phone);

            // 构建登录请求体
            String loginBody = String.format(
                    "{\"phone\":\"%s\",\"password\":\"%s\",\"deviceType\":3,\"deviceInfo\":\"MacChrome/143.0.0.0\",\"terminalAppType\":201}",
                    phone, password
            );

            // 构建请求头
            Map<String, Object> config = new HashMap<>();
            config.put("url", "https://ydtwx.cloud.haizhikao.com/api/manage-app/app/user/login");
            config.put("Accept", "application/json, text/plain, */*");
            config.put("Accept-Language", "zh-CN,zh;q=0.9");
            config.put("Content-Type", "application/json; charset=UTF-8");
            config.put("Origin", "https://ydtwx.cloud.haizhikao.com");
            config.put("Referer", "https://ydtwx.cloud.haizhikao.com/");
            config.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36");

            String responseBody = HttpUtils.doPost(loginBody, config);
            logger.debug("响应内容: {}", responseBody);

            JsonNode root = objectMapper.readTree(responseBody);

            // 检查业务状态码（这个API code: 1 表示成功）
            int code = root.has("code") ? root.get("code").asInt() : 0;
            boolean success = root.has("success") && root.get("success").asBoolean();

            if (code != 1 || !success) {
                String message = root.has("msg") ? root.get("msg").asText() :
                        root.has("message") ? root.get("message").asText() : "登录失败";
                logger.error("登录失败: {} (code: {})", message, code);
                return false;
            }

            // 提取 token 和数据
            JsonNode data = root.get("data");
            if (data != null) {
                // 提取 token
                if (data.has("token")) {
                    accessToken = data.get("token").asText();
                    logger.info("✓ 获取到 token: {}...", accessToken.substring(0, Math.min(30, accessToken.length())));
                }

                // 提取用户信息
                String userName = data.has("name") ? data.get("name").asText() :
                        data.has("nickname") ? data.get("nickname").asText() : "未知";
                logger.info("✓ 用户: {} (ID: {})", userName, data.has("userId") ? data.get("userId").asText() : "N/A");
            }

            logger.info("✓ 登录成功！");
            return true;

        } catch (Exception e) {
            logger.error("登录异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 发送带认证的请求
     *
     * @param url 请求地址
     * @return 响应内容，失败返回 null
     */
    public String
    sendAuthenticatedRequest(String url) {
        try {
            // 构建请求头
            Map<String, Object> config = new HashMap<>();
            config.put("Accept", "application/json, text/plain, */*");
            config.put("Accept-Language", "zh-CN,zh;q=0.9");
            config.put("Content-Type", "application/json; charset=UTF-8");
            config.put("Origin", "https://ydtwx.cloud.haizhikao.com");
            config.put("Referer", "https://ydtwx.cloud.haizhikao.com/");
            config.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36");

            // 添加 Cookie
            if (loginCookie != null) {
                config.put("Cookie", loginCookie);
            }

            // 添加 Token
            if (accessToken != null) {
                config.put("x-access-token", accessToken);
            }

            return HttpUtils.doGet(url, config);
        } catch (IOException e) {
            logger.error("请求失败: {} - {}", url, e.getMessage());
            return null;
        }
    }

    /**
     * 获取访问 Token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * 获取登录 Cookie
     */
    public String getLoginCookie() {
        return loginCookie;
    }

    /**
     * 检查是否已登录
     */
    public boolean isLoggedIn() {
        return accessToken != null || loginCookie != null;
    }
}
