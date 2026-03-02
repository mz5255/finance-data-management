package cn.com.mz.app.finance.module.filter;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义认证拦截器
 *
 * @author mz
 */
@Component
@Order(1)
public class AuthFilter implements Filter {

    private static final List<String> WHITE_LIST = Arrays.asList(
            "/",
            "/swagger-ui",
            "/v3/api-docs",
            "/api/finance-data/auth/login",
            "/api/finance-data/auth/register",
            "/api/finance-data/auth/captchaImage"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        // 白名单放行
        if (isWhiteList(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        // 检查token
        try {
            StpUtil.checkLogin();
            chain.doFilter(request, response);
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"code\":401,\"message\":\"未登录或token已过期\"}");
        }
    }

    private boolean isWhiteList(String uri) {
        return WHITE_LIST.stream().anyMatch(uri::startsWith);
    }
}
