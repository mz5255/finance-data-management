package cn.com.mz.app.finance.module.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 *
 * @author mz
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermissions {

    /**
     * 需要校验的权限码
     */
    String[] value() default {};

    /**
     * 验证模式：AND | OR，默认AND
     */
    Logical logical() default Logical.AND;

    /**
     * 验证逻辑
     */
    enum Logical {
        /**
         * 必须具有所有的元素
         */
        AND,

        /**
         * 只需具有其中一个元素
         */
        OR
    }
}