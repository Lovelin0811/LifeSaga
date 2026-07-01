package com.lovelin.lifesaga.shared.infrastructure.security;

import com.lovelin.lifesaga.identity.infrastructure.security.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    public AuthFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtTokenService.validateToken(token)) {
                request.setAttribute("userId", jwtTokenService.getUserIdFromToken(token));
                filterChain.doFilter(request, response);
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"data\":null,\"message\":\"未登录或token已过期\"}");
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/")
                || "/api/sagas/public".equals(path)
                || "/api/sagas/public/".equals(path);
    }
}
