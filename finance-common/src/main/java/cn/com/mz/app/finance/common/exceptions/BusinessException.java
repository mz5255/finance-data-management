package cn.com.mz.app.finance.common.exceptions;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.common.exceptions
 * @date 2025/9/13 14:42
 * @description: 业务异常处理器
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
