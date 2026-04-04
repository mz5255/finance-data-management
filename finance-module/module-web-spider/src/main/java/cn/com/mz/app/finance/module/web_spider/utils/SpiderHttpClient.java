package cn.com.mz.app.finance.module.web_spider.utils;

import cn.hutool.core.map.MapUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 爬虫专用 HTTP 客户端
 * <p>
 * 特性：
 * - 连接池复用，提升性能
 * - 超时配置（连接、读取、写入）
 * - 资源自动关闭（try-with-resources）
 * - 自动重试机制
 * - 代理支持
 * - SSL 证书信任（用于 HTTPS 站点）
 * </p>
 */
@Slf4j
public class SpiderHttpClient implements AutoCloseable {

    private final CloseableHttpClient httpClient;
    private final PoolingHttpClientConnectionManager connectionManager;
    private final SpiderHttpConfig config;

    /**
     * 创建默认配置的客户端
     */
    public SpiderHttpClient() {
        this(SpiderHttpConfig.builder().build());
    }

    /**
     * 创建自定义配置的客户端
     */
    public SpiderHttpClient(SpiderHttpConfig config) {
        this.config = config;
        this.connectionManager = createConnectionManager();
        this.httpClient = createHttpClient();
    }

    /**
     * 创建连接管理器
     */
    private PoolingHttpClientConnectionManager createConnectionManager() {
        try {
            // 信任所有证书
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true)
                    .build();

            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    sslContext, NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslSocketFactory)
                    .build();

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                    socketFactoryRegistry,
                    null,
                    null,
                    null,
                    config.getConnTimeToLive(),
                    TimeUnit.MILLISECONDS);

            // 最大连接数
            cm.setMaxTotal(config.getMaxTotalConnections());
            // 每个路由的最大连接数
            cm.setDefaultMaxPerRoute(config.getMaxConnectionsPerRoute());

            return cm;
        } catch (Exception e) {
            throw new RuntimeException("创建连接管理器失败", e);
        }
    }

    /**
     * 创建 HTTP 客户端
     */
    private CloseableHttpClient createHttpClient() {
        HttpClientBuilder builder = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setConnectionManagerShared(false)
                .evictIdleConnections(config.getIdleConnTimeout(), TimeUnit.MILLISECONDS)
                .setRetryHandler((exception, executionCount, context) -> {
                    // 重试逻辑
                    if (executionCount > config.getMaxRetries()) {
                        log.warn("已达到最大重试次数: {}", config.getMaxRetries());
                        return false;
                    }
                    if (exception instanceof org.apache.http.NoHttpResponseException) {
                        log.warn("请求无响应，进行第 {} 次重试", executionCount);
                        return true;
                    }
                    if (exception instanceof java.net.SocketTimeoutException) {
                        log.warn("请求超时，进行第 {} 次重试", executionCount);
                        return config.isRetryOnTimeout();
                    }
                    if (exception instanceof javax.net.ssl.SSLException) {
                        log.warn("SSL 异常，进行第 {} 次重试", executionCount);
                        return true;
                    }
                    return false;
                });

        // 设置代理
        if (config.getProxyHost() != null && config.getProxyPort() != null) {
            HttpHost proxy = new HttpHost(config.getProxyHost(), config.getProxyPort());
            builder.setProxy(proxy);
        }

        // 设置默认 User-Agent
        if (StringUtils.isNotBlank(config.getDefaultUserAgent())) {
            builder.setUserAgent(config.getDefaultUserAgent());
        }

        return builder.build();
    }

    /**
     * 创建请求配置
     */
    private RequestConfig createRequestConfig() {
        RequestConfig.Builder configBuilder = RequestConfig.custom()
                .setConnectTimeout((int) this.config.getConnectTimeout())
                .setSocketTimeout((int) this.config.getSocketTimeout())
                .setConnectionRequestTimeout((int) this.config.getConnectionRequestTimeout());

        // 代理配置（如果设置）
        if (this.config.getProxyHost() != null && this.config.getProxyPort() != null) {
            HttpHost proxy = new HttpHost(this.config.getProxyHost(), this.config.getProxyPort());
            configBuilder.setProxy(proxy);
        }

        return configBuilder.build();
    }

    /**
     * 执行 GET 请求
     *
     * @param url     请求地址
     * @param headers 请求头
     * @return 响应内容
     */
    public HttpResponse doGet(String url, Map<String, String> headers) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        setHeaders(httpGet, headers);
        httpGet.setConfig(createRequestConfig());

        return executeWithRetry(httpGet);
    }

    /**
     * 执行 GET 请求（无自定义请求头）
     */
    public HttpResponse doGet(String url) throws IOException {
        return doGet(url, null);
    }

    /**
     * 执行 POST 请求
     *
     * @param url     请求地址
     * @param body    请求体
     * @param headers 请求头
     * @return 响应内容
     */
    public HttpResponse doPost(String url, String body, Map<String, String> headers) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        setHeaders(httpPost, headers);
        httpPost.setConfig(createRequestConfig());

        if (StringUtils.isNotBlank(body)) {
            StringEntity entity = new StringEntity(body, StandardCharsets.UTF_8);
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
        }

        return executeWithRetry(httpPost);
    }

    /**
     * 执行 POST 请求（无自定义请求头）
     */
    public HttpResponse doPost(String url, String body) throws IOException {
        return doPost(url, body, null);
    }

    /**
     * 设置请求头
     */
    private void setHeaders(HttpRequestBase request, Map<String, String> headers) {
        // 设置默认请求头
        request.setHeader("Accept", "application/json, text/plain, */*");
        request.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");

        if (StringUtils.isNotBlank(config.getDefaultUserAgent())) {
            request.setHeader("User-Agent", config.getDefaultUserAgent());
        } else {
            request.setHeader("User-Agent", getDefaultUserAgent());
        }

        // 设置自定义请求头
        if (MapUtil.isNotEmpty(headers)) {
            headers.forEach(request::setHeader);
        }
    }

    /**
     * 带重试的请求执行
     */
    private HttpResponse executeWithRetry(HttpRequestBase request) throws IOException {
        int retryCount = 0;
        IOException lastException = null;

        while (retryCount <= config.getMaxRetries()) {
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                return HttpResponse.builder()
                        .statusCode(response.getStatusLine().getStatusCode())
                        .body(content)
                        .headers(extractHeaders(response))
                        .build();
            } catch (IOException e) {
                lastException = e;
                retryCount++;

                if (retryCount <= config.getMaxRetries()) {
                    log.warn("请求失败，第 {} 次重试: {} - {}", retryCount, request.getURI(), e.getMessage());
                    try {
                        Thread.sleep(config.getRetryInterval());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("请求被中断", ie);
                    }
                }
            }
        }

        throw new IOException("请求失败，已重试 " + config.getMaxRetries() + " 次: " + request.getURI(), lastException);
    }

    /**
     * 提取响应头
     */
    private Map<String, String> extractHeaders(CloseableHttpResponse response) {
        Map<String, String> headers = new HashMap<>();
        for (org.apache.http.Header header : response.getAllHeaders()) {
            headers.put(header.getName(), header.getValue());
        }
        return headers;
    }

    /**
     * 获取默认 User-Agent
     */
    private String getDefaultUserAgent() {
        return "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    }

    /**
     * 获取连接池状态
     */
    public String getPoolStats() {
        return String.format("连接池状态 - 总连接: %d, 可用: %d, 等待: %d, 最大: %d",
                connectionManager.getTotalStats().getLeased(),
                connectionManager.getTotalStats().getAvailable(),
                connectionManager.getTotalStats().getPending(),
                connectionManager.getTotalStats().getMax());
    }

    @Override
    public void close() {
        try {
            if (httpClient != null) {
                httpClient.close();
            }
            if (connectionManager != null) {
                connectionManager.close();
            }
            log.info("SpiderHttpClient 已关闭");
        } catch (IOException e) {
            log.error("关闭 SpiderHttpClient 失败", e);
        }
    }

    /**
     * HTTP 响应封装
     */
    @Builder
    @Getter
    public static class HttpResponse {
        private final int statusCode;
        private final String body;
        private final Map<String, String> headers;

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public boolean isRedirect() {
            return statusCode >= 300 && statusCode < 400;
        }

        public boolean isClientError() {
            return statusCode >= 400 && statusCode < 500;
        }

        public boolean isServerError() {
            return statusCode >= 500;
        }
    }

    /**
     * 配置类
     */
    @Builder
    @Getter
    @Setter
    public static class SpiderHttpConfig {
        /**
         * 最大总连接数
         */
        @Builder.Default
        private int maxTotalConnections = 100;

        /**
         * 每个路由最大连接数
         */
        @Builder.Default
        private int maxConnectionsPerRoute = 20;

        /**
         * 连接超时时间（毫秒）
         */
        @Builder.Default
        private long connectTimeout = 10000;

        /**
         * 读取超时时间（毫秒）
         */
        @Builder.Default
        private long socketTimeout = 30000;

        /**
         * 从连接池获取连接的超时时间（毫秒）
         */
        @Builder.Default
        private long connectionRequestTimeout = 5000;

        /**
         * 连接存活时间（毫秒）
         */
        @Builder.Default
        private long connTimeToLive = 60000;

        /**
         * 空闲连接超时时间（毫秒）
         */
        @Builder.Default
        private long idleConnTimeout = 30000;

        /**
         * 最大重试次数
         */
        @Builder.Default
        private int maxRetries = 3;

        /**
         * 重试间隔（毫秒）
         */
        @Builder.Default
        private long retryInterval = 1000;

        /**
         * 超时是否重试
         */
        @Builder.Default
        private boolean retryOnTimeout = true;

        /**
         * 代理主机
         */
        private String proxyHost;

        /**
         * 代理端口
         */
        private Integer proxyPort;

        /**
         * 默认 User-Agent
         */
        private String defaultUserAgent;
    }
}
