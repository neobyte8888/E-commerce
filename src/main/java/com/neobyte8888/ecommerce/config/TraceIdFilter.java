package com.neobyte8888.ecommerce.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            String traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);

            HttpServletRequest req = (HttpServletRequest) request;
            MDC.put("path", req.getRequestURI());
            MDC.put("method", req.getMethod());

            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}