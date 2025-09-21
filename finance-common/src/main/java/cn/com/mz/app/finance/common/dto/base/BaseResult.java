package cn.com.mz.app.finance.common.dto.base;

import lombok.Data;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.common.dto
 * @date 2025/9/13 14:44
 * @description: 功能描述
 */
@Data
public class BaseResult<T> {
    private Integer code;
    private String message;
    private T data;
    private String traceId;

    public static <T> BaseResult<T> success() {
        return new BaseResult<T>();
    }

    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<>(data);
    }

    public static <T> BaseResult<T> error(int code, String msg, T data) {
        return new BaseResult<>(code, msg, data);
    }

    public static <T> BaseResult<T> error(int code, String msg) {
        return new BaseResult<>(code, msg);
    }

    public boolean isSuccess() {
        return this.code == 200;
    }

    public BaseResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResult(T data) {
        this.code = 200;
        this.message = "success";
        this.data = data;
    }

    public BaseResult() {
        this.code = 200;
        this.message = "success";
        this.data = null;
    }

    public BaseResult(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
