package cn.com.mz.app.finance.module.web_spider.video;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * M3U8 文件信息
 *
 * @author mz
 * @date 2026/3/17
 */
@Data
@Builder
public class M3U8Info {

    /**
     * 是否加密
     */
    private boolean encrypted;

    /**
     * 加密方法（如 AES-128）
     */
    private String encryptMethod;

    /**
     * 解密 Key 的 URL
     */
    private String keyUrl;

    /**
     * 解密 Key（16 字节）
     */
    private byte[] decryptKey;

    /**
     * 初始化向量（IV）
     */
    private String iv;

    /**
     * ts 分片列表
     */
    @Builder.Default
    private List<TsSegment> segments = new ArrayList<>();

    /**
     * 每个分片的时长（秒）
     */
    private double targetDuration;

    /**
     * 总时长（秒）
     */
    private double totalDuration;

    /**
     * 计算总时长
     */
    public void calculateTotalDuration() {
        this.totalDuration = segments.stream()
                .mapToDouble(TsSegment::getDuration)
                .sum();
    }

    /**
     * 获取分片数量
     */
    public int getSegmentCount() {
        return segments != null ? segments.size() : 0;
    }

    /**
     * 下载状态
     */
    public enum DownloadStatus {
        PENDING,
        DOWNLOADING,
        COMPLETED,
        FAILED
    }

    /**
     * ts 分片信息
     */
    @Data
    @Builder
    public static class TsSegment {
        /**
         * 分片序号
         */
        private int index;

        /**
         * 分片 URL（可能是相对路径或完整 URL）
         */
        private String url;

        /**
         * 分片时长（秒）
         */
        private double duration;

        /**
         * 下载状态
         */
        @Builder.Default
        private DownloadStatus status = DownloadStatus.PENDING;

        /**
         * 本地临时文件路径
         */
        private String localPath;
    }
}
