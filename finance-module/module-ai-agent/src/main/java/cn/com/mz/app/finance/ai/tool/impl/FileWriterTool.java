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
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件写入工具
 * 写入或创建文件
 *
 * @author mz
 */
@Component
@RequiredArgsConstructor
public class FileWriterTool implements Tool {

    private final AiProperties aiProperties;

    @Override
    public String getName() {
        return "file_writer";
    }

    @Override
    public String getDescription() {
        return "写入文件内容，支持创建、追加和覆盖模式";
    }

    @Override
    public String getCategory() {
        return "file";
    }

    @Override
    public RiskLevel getRiskLevel() {
        return RiskLevel.HIGH;
    }

    @Override
    public boolean requireConfirmation() {
        AiProperties.ToolConfig toolConfig = aiProperties.getTools().get("file_writer");
        return toolConfig == null || toolConfig.isRequireConfirmation();
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("path", Map.of(
                "type", "string",
                "description", "文件路径（相对于项目根目录）"
        ));
        properties.put("content", Map.of(
                "type", "string",
                "description", "文件内容"
        ));
        properties.put("mode", Map.of(
                "type", "string",
                "description", "写入模式: create(创建新文件)/append(追加)/overwrite(覆盖)",
                "enum", List.of("create", "append", "overwrite"),
                "default", "create"
        ));

        return Map.of(
                "type", "object",
                "properties", properties,
                "required", List.of("path", "content")
        );
    }

    @Override
    public ToolResult execute(Map<String, Object> params) {
        String filePath = (String) params.get("path");
        String content = (String) params.get("content");
        String mode = (String) params.getOrDefault("mode", "create");

        if (filePath == null || filePath.isBlank()) {
            return ToolResult.error("文件路径不能为空");
        }

        if (content == null) {
            return ToolResult.error("文件内容不能为空");
        }

        try {
            AiProperties.ToolConfig toolConfig = aiProperties.getTools().get("file_writer");
            String projectRoot = toolConfig != null ? toolConfig.getProjectRoot() : "./";

            Path fullPath = Paths.get(projectRoot, filePath).normalize();

            // 安全检查：确保路径在项目目录内
            if (!fullPath.startsWith(Paths.get(projectRoot).normalize())) {
                return ToolResult.error("文件路径超出允许范围");
            }

            // 检查目录限制
            if (toolConfig != null && !toolConfig.getAllowedDirectories().isEmpty()) {
                boolean inAllowedDir = toolConfig.getAllowedDirectories().stream()
                        .anyMatch(dir -> fullPath.startsWith(Paths.get(projectRoot, dir).normalize()));
                if (!inAllowedDir) {
                    return ToolResult.error("文件路径不在允许的目录内");
                }
            }

            // 根据模式处理
            boolean fileExists = Files.exists(fullPath);
            StandardOpenOption openOption;

            switch (mode) {
                case "create":
                    if (fileExists) {
                        return ToolResult.error("文件已存在: " + filePath);
                    }
                    // 创建父目录
                    Files.createDirectories(fullPath.getParent());
                    Files.writeString(fullPath, content, StandardCharsets.UTF_8);
                    break;

                case "append":
                    if (!fileExists) {
                        Files.createDirectories(fullPath.getParent());
                    }
                    Files.writeString(fullPath, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    break;

                case "overwrite":
                    if (!fileExists) {
                        Files.createDirectories(fullPath.getParent());
                    }
                    Files.writeString(fullPath, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    break;

                default:
                    return ToolResult.error("未知的写入模式: " + mode);
            }

            long fileSize = Files.size(fullPath);

            return ToolResult.success(Map.of(
                    "path", filePath,
                    "mode", mode,
                    "size", fileSize,
                    "action", fileExists ? "modified" : "created"
            ));

        } catch (IOException e) {
            return ToolResult.error("写入文件失败: " + e.getMessage());
        }
    }
}
