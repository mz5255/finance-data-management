package cn.com.mz.app;


import cn.com.mz.app.response.SmsSendResponse;

/**
 * 短信服务
 *
 * @author mz
 */
public interface SmsService {
    /**
     * 发送短信
     *
     * @param phoneNumber
     * @param code
     * @return
     */
    SmsSendResponse sendMsg(String phoneNumber, String code);
}
