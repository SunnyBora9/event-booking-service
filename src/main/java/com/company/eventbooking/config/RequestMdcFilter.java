package com.company.eventbooking.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestMdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Generate a request-specific ID (Correlation ID)
            MDC.put("requestId", UUID.randomUUID().toString());

            // Get or create a session to retrieve the Session ID
            // true forces the creation of a session if one doesn't exist
            String sessionId = request.getSession(true).getId();

            // Put the Session ID into the MDC 'user' key for your log pattern [%X{user}]
            MDC.put("user", sessionId);

            MDC.put("path", request.getRequestURI());

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}