package cn.com.mz.app.finance.module.web_spider.video;

import cn.com.mz.app.finance.module.web_spider.utils.SpiderHttpClient;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * M3U8 视频下载器
 * 支持 AES-128 加密视频的下载和解密
 *
 * @author mz
 * @date 2026/3/17
 */
@Slf4j
public class M3U8Downloader implements Closeable {

    private final SpiderHttpClient httpClient;
    private final M3U8Parser parser;
    private final ExecutorService executorService;
    private final DownloadConfig config;

    public M3U8Downloader() {
        this(DownloadConfig.builder().build());
    }

    public M3U8Downloader(DownloadConfig config) {
        this.config = config;
        this.httpClient = new SpiderHttpClient();
        this.parser = new M3U8Parser();
        this.executorService = Executors.newFixedThreadPool(config.getThreadCount());
    }

    /**
     * 下载视频
     *
     * @param m3u8Url    m3u8 文件 URL
     * @param outputPath 输出文件路径（如 /path/to/video.mp4）
     * @return 下载结果
     */
    public DownloadResult download(String m3u8Url, String outputPath) {
        return download(m3u8Url, outputPath, null);
    }

    /**
     * 下载视频（带进度回调）
     *
     * @param m3u8Url    m3u8 文件 URL
     * @param outputPath 输出文件路径
     * @param callback   进度回调
     * @return 下载结果
     */
    public DownloadResult download(String m3u8Url, String outputPath, ProgressCallback callback) {
        long startTime = System.currentTimeMillis();
        log.info("开始下载视频: {}", m3u8Url);
        log.info("输出路径: {}", outputPath);

        try {
            // 1. 下载 m3u8 文件
            SpiderHttpClient.HttpResponse m3u8Response = httpClient.doGet(m3u8Url);
            if (!m3u8Response.isSuccess()) {
                return DownloadResult.fail("下载 m3u8 失败: HTTP " + m3u8Response.getStatusCode());
            }

            // 2. 解析 m3u8
            M3U8Info m3u8Info = parser.parse(m3u8Response.getBody(), m3u8Url);
            log.info("视频信息: 分片数={}, 时长={}秒, 加密={}",
                    m3u8Info.getSegmentCount(), (int) m3u8Info.getTotalDuration(), m3u8Info.isEncrypted());

            // 3. 获取解密 Key（如果加密）
            byte[] decryptKey = null;
            if (m3u8Info.isEncrypted() && StringUtils.isNotBlank(m3u8Info.getKeyUrl())) {
                decryptKey = fetchDecryptKey(m3u8Info.getKeyUrl());
                if (decryptKey == null) {
                    return DownloadResult.fail("获取解密 Key 失败");
                }
                log.info("成功获取解密 Key ({} 字节)", decryptKey.length);
            }

            // 4. 创建临时目录
            Path tempDir = Files.createTempDirectory("m3u8_download_");
            log.info("临时目录: {}", tempDir);

            // 5. 并发下载 ts 分片
            List<File> tsFiles = downloadSegments(m3u8Info, decryptKey, tempDir, callback);

            // 6. 合并文件
            mergeFiles(tsFiles, outputPath);

            // 7. 清理临时文件
            cleanupTempDir(tempDir);

            long duration = System.currentTimeMillis() - startTime;
            log.info("下载完成! 耗时: {} 秒, 输出: {}", duration / 1000, outputPath);

            return DownloadResult.success(outputPath, m3u8Info.getTotalDuration(), duration);

        } catch (Exception e) {
            log.error("下载失败: {}", e.getMessage(), e);
            return DownloadResult.fail("下载失败: " + e.getMessage());
        }
    }

    /**
     * 获取解密 Key
     */
    private byte[] fetchDecryptKey(String keyUrl) {
        try {
            SpiderHttpClient.HttpResponse response = httpClient.doGet(keyUrl);
            if (response.isSuccess()) {
                return response.getBody().getBytes("ISO-8859-1"); // Key 通常是原始字节
            }
        } catch (Exception e) {
            log.error("获取解密 Key 失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 并发下载所有 ts 分片
     */
    private List<File> downloadSegments(M3U8Info m3u8Info, byte[] decryptKey, Path tempDir, ProgressCallback callback) {
        int totalSegments = m3u8Info.getSegmentCount();
        AtomicInteger completedCount = new AtomicInteger(0);

        List<CompletableFuture<File>> futures = new ArrayList<>();
        File[] resultFiles = new File[totalSegments];

        for (M3U8Info.TsSegment segment : m3u8Info.getSegments()) {
            CompletableFuture<File> future = CompletableFuture.supplyAsync(() -> {
                try {
                    // 添加延迟，避免请求过快
                    if (config.getDelayMs() > 0) {
                        Thread.sleep(config.getDelayMs());
                    }

                    File tsFile = downloadAndDecryptSegment(segment, decryptKey, tempDir);
                    resultFiles[segment.getIndex()] = tsFile;

                    int completed = completedCount.incrementAndGet();
                    if (callback != null) {
                        callback.onProgress(completed, totalSegments, segment.getIndex());
                    }

                    return tsFile;
                } catch (Exception e) {
                    log.error("下载分片 {} 失败: {}", segment.getIndex(), e.getMessage());
                    return null;
                }
            }, executorService);

            futures.add(future);
        }

        // 等待所有下载完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 按顺序返回文件列表
        List<File> files = new ArrayList<>();
        for (File file : resultFiles) {
            if (file != null) {
                files.add(file);
            }
        }

        log.info("分片下载完成: {}/{}", files.size(), totalSegments);
        return files;
    }

    /**
     * 下载并解密单个 ts 分片
     */
    private File downloadAndDecryptSegment(M3U8Info.TsSegment segment, byte[] decryptKey, Path tempDir) throws Exception {
        // 下载 ts 文件
        SpiderHttpClient.HttpResponse response = httpClient.doGet(segment.getUrl());
        if (!response.isSuccess()) {
            throw new IOException("下载失败: HTTP " + response.getStatusCode());
        }

        byte[] data = response.getBody().getBytes("ISO-8859-1");

        // 解密（如果需要）
        if (decryptKey != null) {
            data = decrypt(data, decryptKey, segment.getIndex());
        }

        // 保存到临时文件
        File tsFile = tempDir.resolve(String.format("%05d.ts", segment.getIndex())).toFile();
        Files.write(tsFile.toPath(), data);

        return tsFile;
    }

    /**
     * AES-128 解密
     */
    private byte[] decrypt(byte[] data, byte[] key, int index) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        // IV 通常是分片序号的 16 字节表示
        byte[] iv = new byte[16];
        String ivHex = String.format("%032x", index);
        for (int i = 0; i < 16; i++) {
            iv[i] = (byte) Integer.parseInt(ivHex.substring(i * 2, i * 2 + 2), 16);
        }

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        return cipher.doFinal(data);
    }

    /**
     * 合并所有 ts 文件
     */
    private void mergeFiles(List<File> tsFiles, String outputPath) throws IOException {
        log.info("开始合并 {} 个分片...", tsFiles.size());

        // 确保输出目录存在
        Path outputDir = Paths.get(outputPath).getParent();
        if (outputDir != null) {
            Files.createDirectories(outputDir);
        }

        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputPath))) {
            for (File tsFile : tsFiles) {
                Files.copy(tsFile.toPath(), outputStream);
            }
        }

        log.info("合并完成: {}", outputPath);
    }

    /**
     * 清理临时目录
     */
    private void cleanupTempDir(Path tempDir) {
        try {
            Files.walk(tempDir)
                    .sorted((a, b) -> -a.compareTo(b))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            log.warn("删除临时文件失败: {}", path);
                        }
                    });
            log.info("临时文件已清理");
        } catch (IOException e) {
            log.warn("清理临时目录失败: {}", e.getMessage());
        }
    }

    @Override
    public void close() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        httpClient.close();
    }

    /**
     * 进度回调接口
     */
    public interface ProgressCallback {
        void onProgress(int completed, int total, int currentIndex);
    }

    /**
     * 下载配置
     */
    @Data
    @Builder
    public static class DownloadConfig {
        @Builder.Default
        private int threadCount = 3;

        @Builder.Default
        private int delayMs = 200; // 请求间隔

        @Builder.Default
        private int maxRetries = 3;
    }

    /**
     * 下载结果
     */
    @Data
    @Builder
    public static class DownloadResult {
        private boolean success;
        private String message;
        private String outputPath;
        private double duration; // 视频时长（秒）
        private long downloadTime; // 下载耗时（毫秒）

        public static DownloadResult success(String outputPath, double duration, long downloadTime) {
            return DownloadResult.builder()
                    .success(true)
                    .outputPath(outputPath)
                    .duration(duration)
                    .downloadTime(downloadTime)
                    .message("下载成功")
                    .build();
        }

        public static DownloadResult fail(String message) {
            return DownloadResult.builder()
                    .success(false)
                    .message(message)
                    .build();
        }
    }
}
