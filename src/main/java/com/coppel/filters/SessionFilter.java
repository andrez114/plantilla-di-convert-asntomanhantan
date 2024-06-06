package com.coppel.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Component
@Order(value = 1)
@RequiredArgsConstructor
public class SessionFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletResponse res = (HttpServletResponse) response;

        res.setContentType("application/json");
        res.setCharacterEncoding(StandardCharsets.UTF_8.toString());

        res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        res.setHeader("Expect-CT", "max-age=3600, enforce");
        res.setHeader("Content-Security-Policy", "script-src 'self' http://cedisdev.coppel.io:20541/dev/sys-name;");

        res.setHeader("X-Frame-Options", "DENY");
        res.setHeader("X-Content-Type-Options", "nosniff");
        filterChain.doFilter(request, res);
    }

}
