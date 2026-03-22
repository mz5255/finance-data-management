package cn.com.mz.app.finance.ai.module;

/**
 * AI 模块接口
 * 所有功能模块都需要实现此接口
 *
 * @author mz
 */
public interface AiModule {

    /**
     * 获取模块名称
     * @return 模块名称
     */
    String getName();

    /**
     * 模块初始化
     */
    void initialize();

    /**
     * 获取模块工厂
     * @return 工厂实例
     */
    AiModuleFactory<?> getFactory();
}
