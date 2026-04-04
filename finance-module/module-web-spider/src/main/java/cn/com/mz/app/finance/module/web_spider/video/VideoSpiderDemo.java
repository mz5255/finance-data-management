package cn.com.mz.app.finance.module.web_spider.video;

import cn.com.mz.app.finance.module.web_spider.video.VideoSpiderService.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 视频爬虫使用示例
 *
 * @author mz
 * @date 2026/3/17
 */
@Slf4j
public class VideoSpiderDemo {

    public static void main(String[] args) {
        // ========== 配置 ==========
        String phone = "15639224316";      // 替换为你的手机号
        String password = "mazhen5255";     // 替换为你的密码
        String saveDir = "./videos";        // 视频保存目录

        // 使用 try-with-resources 确保资源释放
        try (VideoSpiderService spider = new VideoSpiderService()) {

            // ========== 1. 登录 ==========
            log.info("========================================");
            log.info("步骤 1: 登录");
            log.info("========================================");

            if (!spider.login(phone, password)) {
                log.error("登录失败，程序退出");
                return;
            }

            UserInfo userInfo = spider.getUserInfo();
            log.info("登录用户: {} (ID: {})", userInfo.getName(), userInfo.getUserId());

            // ========== 2. 获取课程列表 ==========
            log.info("\n========================================");
            log.info("步骤 2: 获取课程列表");
            log.info("========================================");

            List<Course> courses = spider.getCourseList();
            if (courses.isEmpty()) {
                log.warn("未找到任何课程");
                return;
            }

            for (int i = 0; i < courses.size(); i++) {
                Course course = courses.get(i);
                log.info("{}. {} (进度: {}%)", i + 1, course.getTitle(), course.getProgress());
            }

            // ========== 3. 获取第一门课程的详情 ==========
            log.info("\n========================================");
            log.info("步骤 3: 获取课程详情");
            log.info("========================================");

            Course firstCourse = courses.get(0);
            CourseDetail detail = spider.getCourseDetail(firstCourse.getCourseId());

            if (detail == null || detail.getChapters() == null) {
                log.error("获取课程详情失败");
                return;
            }

            log.info("课程: {}", detail.getTitle());
            log.info("章节数: {}", detail.getChapters().size());

            int chapterIndex = 0;
            for (Chapter chapter : detail.getChapters()) {
                log.info("\n[章节 {}] {}", ++chapterIndex, chapter.getTitle());
                if (chapter.getTasks() != null) {
                    for (VideoTask task : chapter.getTasks()) {
                        log.info("  - {} ({}秒)", task.getTitle(), task.getDuration());
                    }
                }
            }

            // ========== 4. 下载第一个视频 ==========
            log.info("\n========================================");
            log.info("步骤 4: 下载视频");
            log.info("========================================");

            // 获取第一个视频的任务ID
            Long firstTaskId = null;
            String videoTitle = null;
            String chapterTitle = null;

            outer:
            for (Chapter chapter : detail.getChapters()) {
                if (chapter.getTasks() != null && !chapter.getTasks().isEmpty()) {
                    VideoTask task = chapter.getTasks().get(0);
                    firstTaskId = task.getTaskId();
                    videoTitle = task.getTitle();
                    chapterTitle = chapter.getTitle();
                    break outer;
                }
            }

            if (firstTaskId == null) {
                log.warn("未找到可下载的视频");
                return;
            }

            // 获取视频详情（包含 m3u8 地址）
            VideoDetail videoDetail = spider.getVideoDetail(firstTaskId);
            if (videoDetail == null || videoDetail.getM3u8Url() == null) {
                log.error("获取视频地址失败");
                return;
            }

            log.info("准备下载: {}", videoTitle);
            log.info("m3u8 地址: {}", videoDetail.getM3u8Url().substring(0, Math.min(80, videoDetail.getM3u8Url().length())) + "...");

            // 构建输出路径
            String safeTitle = sanitizeFileName(videoTitle);
            String outputPath = String.format("%s/%s/%s.ts", saveDir, sanitizeFileName(detail.getTitle()), safeTitle);

            // 下载视频（带进度回调）
            M3U8Downloader.DownloadResult result = spider.downloadVideo(
                    videoDetail.getM3u8Url(),
                    outputPath,
                    (completed, total, currentIndex) -> {
                        double percent = (double) completed / total * 100;
                        log.info("下载进度: {}/{} ({:.1f}%) - 分片 {}", completed, total, percent, currentIndex);
                    }
            );

            if (result.isSuccess()) {
                log.info("\n========================================");
                log.info("下载成功!");
                log.info("========================================");
                log.info("文件: {}", result.getOutputPath());
                log.info("时长: {} 秒", (int) result.getDuration());
                log.info("耗时: {} 秒", result.getDownloadTime() / 1000);
            } else {
                log.error("下载失败: {}", result.getMessage());
            }

        } catch (Exception e) {
            log.error("程序异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 清理文件名中的非法字符
     */
    private static String sanitizeFileName(String name) {
        if (name == null) return "unnamed";
        return name.replaceAll("[\\\\/:*?\"<>|]", "_")
                .replaceAll("\\s+", "_")
                .trim();
    }
}
