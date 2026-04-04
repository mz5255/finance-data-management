package cn.com.mz.app.finance.module.web_spider.video;

import cn.com.mz.app.finance.module.web_spider.utils.SpiderHttpClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * API 调试工具 - 用于分析 API 响应结构
 *
 * @author mz
 * @date 2026/3/17
 */
@Slf4j
public class ApiDebugRunner {

    private static final String BASE_URL = "https://ydtwx.cloud.haizhikao.com";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        String phone = "15639224316";
        String password = "mazhen5255";

        try (SpiderHttpClient httpClient = new SpiderHttpClient()) {

            // 1. 登录
            log.info("========================================");
            log.info("1. 登录测试");
            log.info("========================================");

            String loginBody = String.format(
                    "{\"phone\":\"%s\",\"password\":\"%s\",\"deviceType\":3,\"deviceInfo\":\"MacChrome/143.0.0.0\",\"terminalAppType\":201}",
                    phone, password);

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json; charset=UTF-8");
            headers.put("Origin", BASE_URL);
            headers.put("Referer", BASE_URL + "/learn-center/");

            SpiderHttpClient.HttpResponse loginResponse = httpClient.doPost(
                    BASE_URL + "/api/manage-app/app/user/login", loginBody, headers);

            log.info("登录响应状态: {}", loginResponse.getStatusCode());
            log.info("登录响应内容: {}", loginResponse.getBody());

            // 提取 Token
            JsonNode loginRoot = objectMapper.readTree(loginResponse.getBody());
            String token = loginRoot.get("data").get("token").asText();
            log.info("Token: {}", token.substring(0, Math.min(50, token.length())) + "...");

            // 2. 获取课程列表
            log.info("\n========================================");
            log.info("2. 课程列表测试");
            log.info("========================================");

            Map<String, String> authHeaders = new HashMap<>();
            authHeaders.put("x-access-token", token);
            authHeaders.put("Origin", BASE_URL);
            authHeaders.put("Referer", BASE_URL + "/learn-center/");

            SpiderHttpClient.HttpResponse courseResponse = httpClient.doGet(
                    BASE_URL + "/api/manage-app/app/user/course/query", authHeaders);

            log.info("课程列表响应状态: {}", courseResponse.getStatusCode());
            log.info("课程列表响应内容: {}", courseResponse.getBody());

            // 3. 获取课程详情
            log.info("\n========================================");
            log.info("3. 课程详情测试 (课程ID: 111244)");
            log.info("========================================");

            SpiderHttpClient.HttpResponse detailResponse = httpClient.doGet(
                    BASE_URL + "/api/manage-app/app/course/detail/111244", authHeaders);

            log.info("课程详情响应状态: {}", detailResponse.getStatusCode());
            log.info("课程详情响应内容: {}", detailResponse.getBody());

            // 4. 获取任务详情
            log.info("\n========================================");
            log.info("4. 任务详情测试 (taskId: 218011)");
            log.info("========================================");

            String taskBody = "{\"courseCatalogTaskId\":218011}";
            Map<String, String> taskHeaders = new HashMap<>(authHeaders);
            taskHeaders.put("Content-Type", "application/json; charset=UTF-8");

            SpiderHttpClient.HttpResponse taskResponse = httpClient.doPost(
                    BASE_URL + "/api/manage-app/app/course/task/detail/query", taskBody, taskHeaders);

            log.info("任务详情响应状态: {}", taskResponse.getStatusCode());
            log.info("任务详情响应内容: {}", taskResponse.getBody());

            // 5. 打印任务详情字段
            log.info("\n========================================");
            log.info("5. 任务详情字段分析");
            log.info("========================================");

            JsonNode taskRoot = objectMapper.readTree(taskResponse.getBody());
            JsonNode taskData = taskRoot.get("data");
            if (taskData != null) {
                log.info("任务数据字段: ");
                Iterator<String> fieldIterator = taskData.fieldNames();
                while (fieldIterator.hasNext()) {
                    String field = fieldIterator.next();
                    log.info("  - {}: {}", field);
                }
            }

        } catch (Exception e) {
            log.error("调试失败: {}", e.getMessage(), e);
        }
    }
}
