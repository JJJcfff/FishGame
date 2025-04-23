package com.example.fishgame.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenVerificationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenUtils tokenUtils;
    
    @Value("${security.token.enabled:true}")
    private boolean tokenVerificationEnabled;
    
    private static final String TOKEN_HEADER = "FishGame-Request-Token";
    private static final String TOKEN_TIMESTAMP_HEADER = "FishGame-Request-Token-Timestamp";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        if (!tokenVerificationEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getRequestURI().contains("generate-token")) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenHeader = request.getHeader(TOKEN_HEADER);
        String timestampHeader = request.getHeader(TOKEN_TIMESTAMP_HEADER);
        
        if (tokenHeader == null || tokenHeader.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing authentication token");
            return;
        }
        
        if (!tokenUtils.validateToken(tokenHeader, timestampHeader)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid authentication token");
            return;
        }

        filterChain.doFilter(request, response);
    }


} 