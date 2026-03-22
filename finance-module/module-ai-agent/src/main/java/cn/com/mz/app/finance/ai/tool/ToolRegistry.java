package cn.com.mz.app.finance.ai.tool;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具注册中心
 * 管理所有可用工具
 *
 * @author mz
 */
@Slf4j
@Component
public class ToolRegistry {

    private final Map<String, Tool> tools = new ConcurrentHashMap<>();
    private final List<Tool> toolList;

    public ToolRegistry(List<Tool> toolList) {
        this.toolList = toolList;
    }

    @PostConstruct
    public void init() {
        toolList.forEach(this::register);
        log.info("Registered {} tools: {}", tools.size(), tools.keySet());
    }

    /**
     * 注册工具
     */
    public void register(Tool tool) {
        tools.put(tool.getName(), tool);
        log.debug("Tool registered: {}", tool.getName());
    }

    /**
     * 注销工具
     */
    public void unregister(String name) {
        tools.remove(name);
        log.debug("Tool unregistered: {}", name);
    }

    /**
     * 获取工具
     */
    public Tool getTool(String name) {
        return tools.get(name);
    }

    /**
     * 检查工具是否存在
     */
    public boolean hasTool(String name) {
        return tools.containsKey(name);
    }

    /**
     * 获取所有工具名称
     */
    public Collection<String> getToolNames() {
        return tools.keySet();
    }

    /**
     * 获取所有工具
     */
    public Collection<Tool> getAllTools() {
        return tools.values();
    }

    /**
     * 根据分类获取工具
     */
    public List<Tool> getToolsByCategory(String category) {
        return tools.values().stream()
                .filter(tool -> category.equals(tool.getCategory()))
                .toList();
    }

    /**
     * 获取工具信息列表（用于 API）
     */
    public List<Map<String, Object>> getToolInfoList() {
        return tools.values().stream()
                .map(tool -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("name", tool.getName());
                    info.put("description", tool.getDescription());
                    info.put("category", tool.getCategory());
                    info.put("riskLevel", tool.getRiskLevel().name());
                    info.put("requireConfirmation", tool.requireConfirmation());
                    info.put("parameters", tool.getParameters());
                    return info;
                })
                .toList();
    }
}
