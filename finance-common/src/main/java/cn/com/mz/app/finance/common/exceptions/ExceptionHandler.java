package cn.com.mz.app.finance.common.exceptions;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.common.exceptions
 * @date 2025/9/13 14:42
 * @description: 统一异常处理
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandler {

    /**
     * 拦截valid 注解指定异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResult<?> validExceptionHandler(MethodArgumentNotValidException e) {
        String msg = getParamErrorMsg(e.getBindingResult());
        log.error("业务异常:"+msg);
        return BaseResult.error(400, msg);
    }
    private String getParamErrorMsg(BindingResult bind) {
        String[] str = Objects.requireNonNull(bind.getAllErrors().get(0).getCodes())[1].split("\\.");
        String message = bind.getAllErrors().get(0).getDefaultMessage();
        String msg1 = "不能为空";
        String msg2 = "不能为null";
        String msg3 = "must not be null";
        String msg4 = "must not be empty";
        if (msg1.equals(message)
                || msg2.equals(message)
                || msg3.equals(message)
                || msg4.equals(message)
        ) {
            message = ArrayUtil.join(ArrayUtil.remove(str, 0), ".") + ":" + message;
        }
        return message;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    public BaseResult<?> BusinessExceptionHandler(BusinessException e) {
        log.error("业务异常:"+e.getMessage());
        return BaseResult.error(400, e.getMessage());
    }


}
