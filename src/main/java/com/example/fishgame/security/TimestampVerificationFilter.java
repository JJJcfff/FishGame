package com.example.fishgame.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Component
public class TimestampVerificationFilter extends OncePerRequestFilter {

    @Value("${security.timestamp.max-diff-minutes:5}")
    private int timestampMaxDiffMin;

    @Value("${security.timestamp.enabled:true}")
    private boolean timestampVerificationEnabled;

    private static final String TIMESTAMP_HEADER = "FishGame-Request-Timestamp";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        if (!timestampVerificationEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String timestampHeader = request.getHeader(TIMESTAMP_HEADER);
        
        if (timestampHeader == null || timestampHeader.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing timestamp header");
            return;
        }
        
        try {
            long timestamp = Long.parseLong(timestampHeader);
            Instant requestTime = Instant.ofEpochMilli(timestamp);
            Instant now = Instant.now();
            
            long diffMin = Math.abs(ChronoUnit.MINUTES.between(requestTime, now));
            
            if (diffMin > timestampMaxDiffMin) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Request timestamp expired or invalid");
                return;
            }

            filterChain.doFilter(request, response);
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid timestamp format");
        }
    }
} 