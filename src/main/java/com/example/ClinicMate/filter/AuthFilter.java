package com.example.ClinicMate.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class AuthFilter implements Filter {

    private final List<String> staticResources = List.of(
            "/script",
            "/style",
            "/img",
            "/imges"
    );

    private final List<String> publicUrls = List.of(
            "/",
            "/index",
            "/header",
            "/footer",
            "/users/signin",
            "/users/signup",
            "/users/action/signin",
            "/users/action/signup",
            "/users/action/signout"
    );

    private final List<String> publicPrefixes = List.of(
            "/users/action",
            "/preview"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();

        // 정적 리소스는 통과
        if (staticResources.stream().anyMatch(uri::startsWith)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 공개 URL 통과
        if (publicUrls.contains(uri) || publicPrefixes.stream().anyMatch(uri::startsWith)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpSession session = request.getSession(false);
        Object loginUser = (session == null) ? null : session.getAttribute("authUser");
        String role = (session == null) ? null : (String) session.getAttribute("role");
        boolean isAdmin = "admin".equalsIgnoreCase(role);

        // 인증 필요 경로: /admin, /users/me 등 보호 자원
        boolean requiresAuth = uri.startsWith("/admin") || uri.startsWith("/users/me");

        if (requiresAuth && loginUser == null) {
            response.sendRedirect("/users/signin");
            return;
        }

        if (!isAdmin && uri.startsWith("/admin")) {
            response.sendRedirect("/");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}