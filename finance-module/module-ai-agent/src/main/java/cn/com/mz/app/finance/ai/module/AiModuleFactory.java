package cn.com.mz.app.finance.ai.module;

/**
 * AI 模块抽象工厂接口
 *
 * @param <T> 工厂创建的对象类型
 * @author mz
 */
public interface AiModuleFactory<T> {

    /**
     * 创建默认实例
     * @return 实例对象
     */
    T createDefault();

    /**
     * 根据配置创建实例
     * @param config 模块配置
     * @return 实例对象
     */
    T create(AiModuleConfig config);

    /**
     * 根据类型创建实例
     * @param type 类型标识
     * @return 实例对象
     */
    T createByType(String type);
}
