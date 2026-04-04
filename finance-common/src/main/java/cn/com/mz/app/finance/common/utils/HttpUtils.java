package cn.com.mz.app.finance.common.utils;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@Slf4j
public class HttpUtils {
    public static String doPost(String body, Map<String, Object> config) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(config.get("url").toString());
        config.forEach((key, value) -> {
            if (!StringUtils.equals("url", key) || !StringUtils.equals("encryptKey", key)) {
                post.setHeader(key, value.toString());
            }
        });
        post.setEntity(new StringEntity(body));
        CloseableHttpResponse response = client.execute(post);
        org.apache.http.HttpEntity entity = response.getEntity();
        String entityString = EntityUtils.toString(entity);
        return entityString;
    }

    public static String doPost(String body, Map<String, Object> config, String url) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        if (!MapUtil.isEmpty(config)) {
            config.forEach((key, value) -> {
                post.setHeader(key, value.toString());
            });
        } else {
            post.setHeader("Content-Type", "application/json");
        }
        post.setEntity(new StringEntity(body));
        CloseableHttpResponse response = client.execute(post);
        org.apache.http.HttpEntity entity = response.getEntity();
        String entityString = EntityUtils.toString(entity);
        return entityString;
    }

    /**
     * GET 请求
     *
     * @param url    请求地址
     * @param config 请求头配置
     * @return 响应内容
     */
    public static String doGet(String url, Map<String, Object> config) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        if (!MapUtil.isEmpty(config)) {
            config.forEach((key, value) -> {
                get.setHeader(key, value.toString());
            });
        } else {
            get.setHeader("Content-Type", "application/json");
        }
        CloseableHttpResponse response = client.execute(get);
        org.apache.http.HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }

    /**
     * 流文件转换成String
     *
     * @param inputStream
     * @return String
     */
    public static String convertStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            log.error("转换异常:{}", e.getMessage(), e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("转换异常:{}", e.getMessage(), e);
            }
        }
        return sb.toString();
    }
}
