package com.example.fishgame.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;

@Component
public class DynamicRateLimiter implements Filter {

    private static final long MAX_REQUESTS_NORMAL = 5; // 正常流量下的请求限制
    private static final long MAX_REQUESTS_HIGH_LOAD = 5; // 高负载下的请求限制

    private HashMap<String, Long> userRequestCount = new HashMap<>();

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        double systemLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();

        long maxRequests = systemLoad > 2.0 ? MAX_REQUESTS_HIGH_LOAD : MAX_REQUESTS_NORMAL;


        synchronized (this) {
            String userId = ((HttpServletRequest) request).getParameter("userId");
            if (userId == null || userId.isEmpty()) {
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"code\": 400, \"msg\": \"Missing userId\"}");
                return;
            }
            userRequestCount.putIfAbsent(userId, 0L);
            userRequestCount.put(userId, userRequestCount.get(userId) + 1);
            if (userRequestCount.get(userId) > maxRequests) {
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"code\": 429, \"msg\": \"Too many requests\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}

