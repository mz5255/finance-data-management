package cn.com.mz.app.finance.ai.tool.impl;

import cn.com.mz.app.finance.ai.config.AiProperties;
import cn.com.mz.app.finance.ai.tool.Tool;
import cn.com.mz.app.finance.ai.tool.ToolResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件读取工具
 * 读取指定文件的内容
 *
 * @author mz
 */
@Component
@RequiredArgsConstructor
public class FileReaderTool implements Tool {

    private final AiProperties aiProperties;

    @Override
    public String getName() {
        return "file_reader";
    }

    @Override
    public String getDescription() {
        return "读取指定文件的内容";
    }

    @Override
    public String getCategory() {
        return "file";
    }

    @Override
    public RiskLevel getRiskLevel() {
        return RiskLevel.LOW;
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("path", Map.of(
                "type", "string",
                "description", "文件路径（相对于项目根目录）"
        ));

        return Map.of(
                "type", "object",
                "properties", properties,
                "required", List.of("path")
        );
    }

    @Override
    public ToolResult execute(Map<String, Object> params) {
        String filePath = (String) params.get("path");
        if (filePath == null || filePath.isBlank()) {
            return ToolResult.error("文件路径不能为空");
        }

        try {
            // 获取项目根目录
            AiProperties.ToolConfig toolConfig = aiProperties.getTools().get("file_reader");
            String projectRoot = toolConfig != null ? toolConfig.getProjectRoot() : "./";

            Path fullPath = Paths.get(projectRoot, filePath).normalize();

            // 安全检查：确保路径在项目目录内
            if (!fullPath.startsWith(Paths.get(projectRoot).normalize())) {
                return ToolResult.error("文件路径超出允许范围");
            }

            // 检查文件扩展名
            String fileName = fullPath.getFileName().toString();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                String extension = fileName.substring(dotIndex);
                if (toolConfig != null && !toolConfig.getAllowedExtensions().isEmpty()) {
                    if (!toolConfig.getAllowedExtensions().contains(extension)) {
                        return ToolResult.error("不支持的文件类型: " + extension);
                    }
                }
            }

            if (!Files.exists(fullPath)) {
                return ToolResult.error("文件不存在: " + filePath);
            }

            if (!Files.isRegularFile(fullPath)) {
                return ToolResult.error("路径不是文件: " + filePath);
            }

            // 检查文件大小
            long fileSize = Files.size(fullPath);
            long maxSize = toolConfig != null ? toolConfig.getMaxFileSize() : 1024 * 1024;
            if (fileSize > maxSize) {
                return ToolResult.error(String.format("文件过大: %d bytes (限制: %d bytes)", fileSize, maxSize));
            }

            String content = Files.readString(fullPath, StandardCharsets.UTF_8);

            return ToolResult.success(Map.of(
                    "path", filePath,
                    "size", fileSize,
                    "content", content
            ));

        } catch (IOException e) {
            return ToolResult.error("读取文件失败: " + e.getMessage());
        }
    }
}
