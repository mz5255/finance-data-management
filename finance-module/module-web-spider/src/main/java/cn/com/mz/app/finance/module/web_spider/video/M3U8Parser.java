package cn.com.mz.app.finance.module.web_spider.video;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * M3U8 文件解析器
 *
 * @author mz
 * @date 2026/3/17
 */
@Slf4j
public class M3U8Parser {

    // #EXT-X-KEY:METHOD=AES-128,URI="xxx",IV=xxx
    private static final Pattern KEY_PATTERN = Pattern.compile(
            "#EXT-X-KEY:METHOD=([^,]+),URI=\"([^\"]+)\"(?:,IV=(.+))?");

    // #EXTINF:30.000000,
    private static final Pattern EXTINF_PATTERN = Pattern.compile("#EXTINF:([\\d.]+)");

    // .ts 文件名
    private static final Pattern TS_PATTERN = Pattern.compile("([^\\s]+\\.ts)");

    /**
     * 解析 m3u8 内容
     *
     * @param m3u8Content m3u8 文件内容
     * @param baseUrl     m3u8 文件的 URL（用于解析相对路径）
     * @return M3U8 信息
     */
    public M3U8Info parse(String m3u8Content, String baseUrl) {
        if (StringUtils.isBlank(m3u8Content)) {
            throw new IllegalArgumentException("m3u8 内容不能为空");
        }

        log.info("开始解析 m3u8 文件...");

        List<M3U8Info.TsSegment> segments = new ArrayList<>();
        boolean encrypted = false;
        String encryptMethod = null;
        String keyUrl = null;
        String iv = null;
        double targetDuration = 0;

        String[] lines = m3u8Content.split("\n");
        double currentDuration = 0;
        int segmentIndex = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // 解析 #EXT-X-TARGETDURATION
            if (line.startsWith("#EXT-X-TARGETDURATION:")) {
                targetDuration = Double.parseDouble(line.substring(22));
                continue;
            }

            // 解析 #EXT-X-KEY（加密信息）
            if (line.startsWith("#EXT-X-KEY:")) {
                Matcher matcher = KEY_PATTERN.matcher(line);
                if (matcher.find()) {
                    encrypted = true;
                    encryptMethod = matcher.group(1);
                    keyUrl = matcher.group(2);
                    iv = matcher.group(3);
                    log.info("检测到加密: method={}, keyUrl={}", encryptMethod, keyUrl);
                }
                continue;
            }

            // 解析 #EXTINF（分片时长）
            if (line.startsWith("#EXTINF:")) {
                Matcher matcher = EXTINF_PATTERN.matcher(line);
                if (matcher.find()) {
                    currentDuration = Double.parseDouble(matcher.group(1));
                }
                continue;
            }

            // 解析 .ts 分片（非 # 开头的行）
            if (!line.startsWith("#") && line.endsWith(".ts")) {
                String tsUrl = resolveUrl(line, baseUrl);

                M3U8Info.TsSegment segment = M3U8Info.TsSegment.builder()
                        .index(segmentIndex++)
                        .url(tsUrl)
                        .duration(currentDuration)
                        .build();

                segments.add(segment);
                currentDuration = 0; // 重置时长
            }
        }

        // 计算总时长
        double totalDuration = segments.stream()
                .mapToDouble(M3U8Info.TsSegment::getDuration)
                .sum();

        log.info("解析完成: 加密={}, 分片数={}, 总时长={}秒",
                encrypted, segments.size(), (int) totalDuration);

        return M3U8Info.builder()
                .encrypted(encrypted)
                .encryptMethod(encryptMethod)
                .keyUrl(keyUrl)
                .iv(iv)
                .targetDuration(targetDuration)
                .segments(segments)
                .totalDuration(totalDuration)
                .build();
    }

    /**
     * 解析相对 URL 为完整 URL
     *
     * @param tsUrl   ts 文件 URL（可能是相对路径）
     * @param baseUrl m3u8 文件的 URL
     * @return 完整的 URL
     */
    private String resolveUrl(String tsUrl, String baseUrl) {
        if (tsUrl.startsWith("http://") || tsUrl.startsWith("https://")) {
            return tsUrl;
        }

        if (StringUtils.isBlank(baseUrl)) {
            return tsUrl;
        }

        // 移除 m3u8 文件名，保留目录路径
        int lastSlashIndex = baseUrl.lastIndexOf('/');
        if (lastSlashIndex > 0) {
            String basePath = baseUrl.substring(0, lastSlashIndex + 1);
            return basePath + tsUrl;
        }

        return tsUrl;
    }

    /**
     * 判断是否是主播放列表（包含多个码率）
     */
    public boolean isMasterPlaylist(String m3u8Content) {
        return m3u8Content.contains("#EXT-X-STREAM-INF");
    }

    /**
     * 从主播放列表中提取指定清晰度的 m3u8 URL
     *
     * @param m3u8Content m3u8 内容
     * @param baseUrl     基础 URL
     * @param quality     清晰度（sd/hd/fhd）
     * @return 子播放列表 URL
     */
    public String extractStreamUrl(String m3u8Content, String baseUrl, String quality) {
        String[] lines = m3u8Content.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.contains("#EXT-X-STREAM-INF")) {
                // 下一行是 m3u8 URL
                if (i + 1 < lines.length) {
                    String streamUrl = lines[i + 1].trim();
                    String fullUrl = resolveUrl(streamUrl, baseUrl);

                    // 根据清晰度选择
                    if (streamUrl.toLowerCase().contains(quality.toLowerCase())) {
                        return fullUrl;
                    }
                }
            }
        }

        // 默认返回第一个
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.endsWith(".m3u8")) {
                return resolveUrl(line, baseUrl);
            }
        }

        return null;
    }
}
