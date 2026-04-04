package cn.com.mz.app.finance.module.web_spider.video;

import cn.com.mz.app.finance.module.web_spider.utils.SpiderHttpClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 海致考试平台视频爬虫服务
 * 整合登录、课程获取、视频下载功能
 *
 * @author mz
 * @date 2026/3/17
 */
@Slf4j
public class VideoSpiderService implements AutoCloseable {

    private static final String BASE_URL = "https://ydtwx.cloud.haizhikao.com";
    private static final String LOGIN_URL = BASE_URL + "/api/manage-app/app/user/login";
    private static final String COURSE_LIST_URL = BASE_URL + "/api/manage-app/app/user/course/query";
    private static final String COURSE_DETAIL_URL = BASE_URL + "/api/manage-app/app/course/detail/";
    private static final String TASK_DETAIL_URL = BASE_URL + "/api/manage-app/app/course/task/detail/query";

    private final SpiderHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final M3U8Downloader downloader;

    private String token;
    private UserInfo userInfo;

    public VideoSpiderService() {
        this.httpClient = new SpiderHttpClient();
        this.objectMapper = new ObjectMapper();
        this.downloader = new M3U8Downloader();
    }

    /**
     * 登录
     *
     * @param phone    手机号
     * @param password 密码
     * @return 是否登录成功
     */
    public boolean login(String phone, String password) {
        try {
            log.info("正在登录: {}", phone);

            String loginBody = String.format(
                    "{\"phone\":\"%s\",\"password\":\"%s\",\"deviceType\":3,\"deviceInfo\":\"MacChrome/143.0.0.0\",\"terminalAppType\":201}",
                    phone, password);

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json; charset=UTF-8");
            headers.put("Origin", BASE_URL);
            headers.put("Referer", BASE_URL + "/learn-center/");

            SpiderHttpClient.HttpResponse response = httpClient.doPost(LOGIN_URL, loginBody, headers);

            if (!response.isSuccess()) {
                log.error("登录失败: HTTP {}", response.getStatusCode());
                return false;
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            int code = root.has("code") ? root.get("code").asInt() : 0;
            boolean success = root.has("success") && root.get("success").asBoolean();

            if (code != 1 || !success) {
                String message = root.has("msg") ? root.get("msg").asText() : "登录失败";
                log.error("登录失败: {}", message);
                return false;
            }

            JsonNode data = root.get("data");
            if (data != null && data.has("token")) {
                this.token = data.get("token").asText();

                this.userInfo = new UserInfo();
                this.userInfo.setUserId(data.has("userId") ? data.get("userId").asText() : null);
                this.userInfo.setName(data.has("name") ? data.get("name").asText() : null);

                log.info("登录成功! 用户: {}", userInfo.getName());
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("登录异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取课程列表
     *
     * @return 课程列表
     */
    public List<Course> getCourseList() {
        checkLogin();

        try {
            log.info("获取课程列表...");

            SpiderHttpClient.HttpResponse response = httpClient.doGet(COURSE_LIST_URL, getAuthHeaders());

            if (!response.isSuccess()) {
                log.error("获取课程列表失败: HTTP {}", response.getStatusCode());
                return new ArrayList<>();
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");

            List<Course> courses = new ArrayList<>();
            if (data != null && data.isArray()) {
                for (JsonNode node : data) {
                    Course course = new Course();
                    // 尝试多种可能的字段名
                    course.setCourseId(getTextField(node, "courseId", "id", "courseId"));
                    course.setTitle(getTextField(node, "courseName", "title", "name", "courseTitle"));
                    course.setCover(getTextField(node, "cover", "coverUrl", "imageUrl"));
                    course.setProgress(node.has("progress") ? node.get("progress").asInt() :
                            node.has("studyProgress") ? node.get("studyProgress").asInt() : 0);
                    courses.add(course);
                }
            }

            log.info("获取到 {} 门课程", courses.size());
            return courses;
        } catch (Exception e) {
            log.error("获取课程列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 打印 API 响应（调试用）
     */
    public void debugPrintResponse(String apiName, String response) {
        log.info("=== {} API Response ===", apiName);
        log.info("{}", response);
        log.info("=== End Response ===");
    }

    /**
     * 获取课程列表（调试模式）
     */
    public List<Course> getCourseListWithDebug() {
        checkLogin();

        try {
            log.info("获取课程列表...");

            SpiderHttpClient.HttpResponse response = httpClient.doGet(COURSE_LIST_URL, getAuthHeaders());

            debugPrintResponse("课程列表", response.getBody());

            if (!response.isSuccess()) {
                log.error("获取课程列表失败: HTTP {}", response.getStatusCode());
                return new ArrayList<>();
            }

            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取课程列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取课程详情（调试模式)
     */
    public CourseDetail getCourseDetailWithDebug(String courseId) {
        checkLogin();

        try {
            log.info("获取课程详情: {}", courseId);

            SpiderHttpClient.HttpResponse response = httpClient.doGet(COURSE_DETAIL_URL + courseId, getAuthHeaders());

            debugPrintResponse("课程详情", response.getBody());

            return null;
        } catch (Exception e) {
            log.error("获取课程详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取课程详情（包含章节和任务）
     *
     * @param courseId 课程ID
     * @return 课程详情
     */
    public CourseDetail getCourseDetail(String courseId) {
        checkLogin();

        try {
            log.info("获取课程详情: {}", courseId);

            SpiderHttpClient.HttpResponse response = httpClient.doGet(COURSE_DETAIL_URL + courseId, getAuthHeaders());

            if (!response.isSuccess()) {
                log.error("获取课程详情失败: HTTP {}", response.getStatusCode());
                return null;
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");

            if (data == null) {
                return null;
            }

            CourseDetail detail = new CourseDetail();
            detail.setCourseId(courseId);
            detail.setTitle(data.has("courseName") ? data.get("courseName").asText() :
                    data.has("title") ? data.get("title").asText() : null);

            // 解析章节
            List<Chapter> chapters = new ArrayList<>();
            JsonNode catalogList = data.has("catalogList") ? data.get("catalogList") :
                    data.has("chapters") ? data.get("chapters") :
                            data.has("list") ? data.get("list") : null;

            if (catalogList != null && catalogList.isArray()) {
                for (JsonNode chapterNode : catalogList) {
                    Chapter chapter = parseChapter(chapterNode);
                    if (chapter != null) {
                        chapters.add(chapter);
                    }
                }
            }

            detail.setChapters(chapters);
            log.info("课程 [{}] 包含 {} 个章节", detail.getTitle(), chapters.size());

            return detail;
        } catch (Exception e) {
            log.error("获取课程详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解析章节数据
     */
    private Chapter parseChapter(JsonNode node) {
        Chapter chapter = new Chapter();
        chapter.setChapterId(node.has("catalogId") ? node.get("catalogId").asText() :
                node.has("chapterId") ? node.get("chapterId").asText() :
                        node.has("id") ? node.get("id").asText() : null);
        chapter.setTitle(node.has("catalogName") ? node.get("catalogName").asText() :
                node.has("chapterName") ? node.get("chapterName").asText() :
                        node.has("name") ? node.get("name").asText() : null);

        // 解析任务（视频）
        List<VideoTask> tasks = new ArrayList<>();
        JsonNode taskList = node.has("taskList") ? node.get("taskList") :
                node.has("tasks") ? node.get("tasks") :
                        node.has("children") ? node.get("children") : null;

        if (taskList != null && taskList.isArray()) {
            for (JsonNode taskNode : taskList) {
                VideoTask task = new VideoTask();
                task.setTaskId(taskNode.has("taskId") ? taskNode.get("taskId").asLong() :
                        taskNode.has("id") ? taskNode.get("id").asLong() : null);
                task.setTitle(taskNode.has("taskName") ? taskNode.get("taskName").asText() :
                        taskNode.has("title") ? taskNode.get("title").asText() :
                                taskNode.has("name") ? taskNode.get("name").asText() : null);
                task.setDuration(taskNode.has("duration") ? taskNode.get("duration").asInt() : 0);
                tasks.add(task);
            }
        }

        chapter.setTasks(tasks);
        return chapter;
    }

    /**
     * 获取视频详情（包含 m3u8 地址）
     *
     * @param taskId 任务ID
     * @return 视频详情
     */
    public VideoDetail getVideoDetail(Long taskId) {
        checkLogin();

        try {
            log.info("获取视频详情: taskId={}", taskId);

            String body = String.format("{\"courseCatalogTaskId\":%d}", taskId);

            Map<String, String> headers = getAuthHeaders();
            headers.put("Content-Type", "application/json; charset=UTF-8");

            SpiderHttpClient.HttpResponse response = httpClient.doPost(TASK_DETAIL_URL, body, headers);

            if (!response.isSuccess()) {
                log.error("获取视频详情失败: HTTP {}", response.getStatusCode());
                return null;
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");

            if (data == null) {
                return null;
            }

            VideoDetail detail = new VideoDetail();
            detail.setTaskId(taskId);
            detail.setTitle(data.has("taskName") ? data.get("taskName").asText() : null);
            detail.setDuration(data.has("duration") ? data.get("duration").asLong() : 0);

            // 获取 m3u8 地址
            String m3u8Url = data.has("videoUrl") ? data.get("videoUrl").asText() :
                    data.has("playUrl") ? data.get("playUrl").asText() :
                            data.has("url") ? data.get("url").asText() : null;

            detail.setM3u8Url(m3u8Url);

            log.info("视频: {}, m3u8: {}", detail.getTitle(), m3u8Url != null ? "已获取" : "未找到");

            return detail;
        } catch (Exception e) {
            log.error("获取视频详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 下载视频
     *
     * @param m3u8Url    m3u8 地址
     * @param outputPath 输出路径
     * @return 下载结果
     */
    public M3U8Downloader.DownloadResult downloadVideo(String m3u8Url, String outputPath) {
        return downloader.download(m3u8Url, outputPath);
    }

    /**
     * 下载视频（带进度回调）
     */
    public M3U8Downloader.DownloadResult downloadVideo(String m3u8Url, String outputPath,
                                                       M3U8Downloader.ProgressCallback callback) {
        return downloader.download(m3u8Url, outputPath, callback);
    }

    /**
     * 获取认证请求头
     */
    private Map<String, String> getAuthHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-access-token", token);
        headers.put("Origin", BASE_URL);
        headers.put("Referer", BASE_URL + "/learn-center/");
        return headers;
    }

    /**
     * 从 JSON 节点中获取文本字段（尝试多个可能的字段名）
     */
    private String getTextField(JsonNode node, String... possibleFields) {
        for (String field : possibleFields) {
            if (node.has(field) && !node.get(field).isNull()) {
                return node.get(field).asText();
            }
        }
        return null;
    }

    /**
     * 从 JSON 节点中获取整数字段
     */
    private Integer getIntField(JsonNode node, String... possibleFields) {
        for (String field : possibleFields) {
            if (node.has(field) && !node.get(field).isNull()) {
                return node.get(field).asInt();
            }
        }
        return null;
    }


    /**
     * 检查是否已登录
     */
    private void checkLogin() {
        if (StringUtils.isBlank(token)) {
            throw new IllegalStateException("请先登录");
        }
    }

    /**
     * 获取当前 Token
     */
    public String getToken() {
        return token;
    }

    /**
     * 获取用户信息
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * 是否已登录
     */
    public boolean isLoggedIn() {
        return StringUtils.isNotBlank(token);
    }

    @Override
    public void close() {
        httpClient.close();
        downloader.close();
    }

    // ========== 实体类 ==========

    @Data
    public static class UserInfo {
        private String userId;
        private String name;
    }

    @Data
    public static class Course {
        private String courseId;
        private String title;
        private String cover;
        private Integer progress;
    }

    @Data
    public static class CourseDetail {
        private String courseId;
        private String title;
        private List<Chapter> chapters;
    }

    @Data
    public static class Chapter {
        private String chapterId;
        private String title;
        private List<VideoTask> tasks;
    }

    @Data
    public static class VideoTask {
        private Long taskId;
        private String title;
        private Integer duration;
    }

    @Data
    public static class VideoDetail {
        private Long taskId;
        private String title;
        private Long duration;
        private String m3u8Url;
    }
}
