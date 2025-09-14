package cn.com.mz.app.finance.common.aspect.log;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.common.enums.ResponseCode;
import cn.com.mz.app.finance.common.exceptions.BusinessException;
import com.alibaba.fastjson.JSONObject;
import jakarta.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Facade的切面处理类，统一统计进行参数校验及异常捕获
 *
 * @author mz
 */
@Aspect
@Component
@Order(Integer.MIN_VALUE)
public class FacadeAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacadeAspect.class);

    @Around("@annotation(cn.com.mz.app.finance.common.aspect.log.Facade)")
    public Object facade(ProceedingJoinPoint pjp) throws Exception {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args = pjp.getArgs();

        Class returnType = ((MethodSignature) pjp.getSignature()).getMethod().getReturnType();

        //循环遍历所有参数，进行参数校验
        for (Object parameter : args) {
            try {
                BeanValidator.validateObject(parameter);
            } catch (ValidationException e) {
                printErrorLog(stopWatch, method, args, "failed to validate", null, e);
                return getFailedResponse(returnType, e);
            }
        }

        Object response = null;
        try {
            response = pjp.proceed();
            // 目标方法执行
            enrichObject(response);
            printInfoLog(stopWatch, method, args, "end to execute", response, null);
            return response;
        } catch (Throwable throwable) {
            // 如果执行异常，则返回一个失败的response
            printErrorLog(stopWatch, method, args, "failed to execute", null, throwable);
//            return getFailedResponse(returnType, throwable);
        }
        return response;
    }

    /**
     * 日志打印
     *
     * @param stopWatch
     * @param method
     * @param args
     * @param action
     * @param response
     */
    private void printInfoLog(StopWatch stopWatch, Method method, Object[] args, String action, Object response,
                          Throwable throwable) {
        try {
            //因为此处有JSON.toJSONString，可能会有异常，需要进行捕获，避免影响主干流程
            LOGGER.info(getInfoMessage(action, stopWatch, method, args, response, throwable), throwable);
            // 如果校验失败，则返回一个失败的response
        } catch (Exception e1) {
            LOGGER.error("log failed", e1);
        }
    }

    /**
     * 日志打印
     *
     * @param stopWatch
     * @param method
     * @param args
     * @param action
     * @param response
     */
    private void printErrorLog(StopWatch stopWatch, Method method, Object[] args, String action, Object response,
                          Throwable throwable) {
        try {
            //因为此处有JSON.toJSONString，可能会有异常，需要进行捕获，避免影响主干流程
            LOGGER.error(getInfoMessage(action, stopWatch, method, args, response, throwable), throwable);
            // 如果校验失败，则返回一个失败的response
        } catch (Exception e1) {
            LOGGER.error("log failed", e1);
        }
    }

    /**
     * 统一格式输出，方便做日志统计
     * <p>
     * *** 如果调整此处的格式，需要同步调整日志监控 ***
     *
     * @param action    行为
     * @param stopWatch 耗时
     * @param method    方法
     * @param args      参数
     * @param response  响应
     * @return 拼接后的字符串
     */
    private String getInfoMessage(String action, StopWatch stopWatch, Method method, Object[] args, Object response,
                                  Throwable exception) {

        StringBuilder stringBuilder = new StringBuilder(action);
        stringBuilder.append(" ,method = ");
        stringBuilder.append(method.getName());
        stringBuilder.append(" ,cost = ");
        stringBuilder.append(stopWatch.getTime()).append(" ms");
        if (response instanceof BaseResult<?>) {
            stringBuilder.append(" ,success = ");
            stringBuilder.append(((BaseResult) response).getCode());
        }
        if (exception != null) {
            stringBuilder.append(" ,success = ");
            stringBuilder.append("400");
        }
        stringBuilder.append(" ,args = ");
        stringBuilder.append(JSONObject.toJSONString(Arrays.toString(args)));

        if (response != null) {
            stringBuilder.append(" ,resp = ");
            stringBuilder.append(JSONObject.toJSONString(response));
        }

        if (exception != null) {
            stringBuilder.append(" ,exception = ");
            stringBuilder.append(exception.getMessage());
        }

        if (response instanceof BaseResult<?>) {
            BaseResult baseResponse = (BaseResult) response;
            if (!baseResponse.getCode().equals(200)) {
                stringBuilder.append(" , execute_failed");
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 将response的信息补全，主要是code和message
     *
     * @param response
     */
    private void enrichObject(Object response) {
        if (response instanceof BaseResult<?>) {
            if (((BaseResult) response).getCode().equals(200)) {
                //如果状态是成功的，需要将未设置的responseCode设置成SUCCESS
                if (StringUtils.isEmpty(((BaseResult) response).getMessage())) {
                    ((BaseResult) response).setMessage(ResponseCode.SUCCESS.name());
                }
            }
        }
    }

    /**
     * 定义并返回一个通用的失败响应
     */
    private Object getFailedResponse(Class returnType, Throwable throwable)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        //如果返回值的类型为BaseResponse 的子类，则创建一个通用的失败响应
        if (returnType.getDeclaredConstructor().newInstance() instanceof BaseResult<?>) {
            BaseResult response = (BaseResult) returnType.getDeclaredConstructor().newInstance();
            response.setCode(200);
            if (throwable instanceof BusinessException bizException) {
                response.setCode(400);
                response.setMessage("业务异常");
            }else {
                response.setMessage(throwable.toString());
                response.setCode(500);
            }

            return response;
        }

        LOGGER.error(
                "failed to getFailedResponse , returnType (" + returnType + ") is not instanceof BaseResponse");
        return null;
    }
}
