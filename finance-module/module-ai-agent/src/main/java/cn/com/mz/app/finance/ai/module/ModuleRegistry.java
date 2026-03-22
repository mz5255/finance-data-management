package cn.com.mz.app.finance.ai.module;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模块注册中心
 * 管理所有 AI 模块的注册和获取
 *
 * @author mz
 */
@Slf4j
@Service
public class ModuleRegistry {

    private final Map<String, AiModule> modules = new ConcurrentHashMap<>();

    /**
     * 注册模块
     * @param module 模块实例
     */
    public void register(AiModule module) {
        String name = module.getName();
        if (modules.containsKey(name)) {
            log.warn("Module {} already registered, will be replaced", name);
        }
        modules.put(name, module);
        log.info("Module {} registered successfully", name);
    }

    /**
     * 注销模块
     * @param name 模块名称
     */
    public void unregister(String name) {
        modules.remove(name);
        log.info("Module {} unregistered", name);
    }

    /**
     * 获取模块
     * @param name 模块名称
     * @return 模块实例
     */
    @SuppressWarnings("unchecked")
    public <T extends AiModule> T getModule(String name) {
        return (T) modules.get(name);
    }

    /**
     * 获取模块工厂
     * @param moduleName 模块名称
     * @return 模块工厂
     */
    @SuppressWarnings("unchecked")
    public <T> AiModuleFactory<T> getFactory(String moduleName) {
        AiModule module = modules.get(moduleName);
        return module != null ? (AiModuleFactory<T>) module.getFactory() : null;
    }

    /**
     * 检查模块是否存在
     * @param name 模块名称
     * @return 是否存在
     */
    public boolean hasModule(String name) {
        return modules.containsKey(name);
    }

    /**
     * 获取所有已注册的模块名称
     * @return 模块名称集合
     */
    public Collection<String> getModuleNames() {
        return modules.keySet();
    }

    /**
     * 初始化所有模块
     */
    public void initializeAll() {
        modules.values().forEach(module -> {
            try {
                module.initialize();
                log.info("Module {} initialized", module.getName());
            } catch (Exception e) {
                log.error("Failed to initialize module: {}", module.getName(), e);
            }
        });
    }
}
