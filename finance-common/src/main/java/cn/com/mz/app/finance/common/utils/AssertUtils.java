package cn.com.mz.app.finance.common.utils;

import cn.com.mz.app.finance.common.exceptions.BusinessException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.common.utils
 * @date 2026/3/1 15:46
 * @description:
 */
public class AssertUtils {
    public static void isNull(Object obj, String message) {
        if (obj == null) {
            throw new BusinessException(message);
        }
    }
    public static void isNotNull(Object obj, String message) {
        if (obj != null) {
            throw new BusinessException(message);
        }
    }

    public static void isBlank(String obj, String message) {
        if (StringUtils.isBlank(obj)) {
            throw new BusinessException(message);
        }
    }
}
