package com.systex.work1.filter;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GeneralFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Check if user is logged in
        Object user = request.getSession().getAttribute("loggedInUser");
        
        // If the request is for the error page, let it through
        if (request.getRequestURI().equals("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        // If the request is for h2-console, let it through
        if (request.getRequestURI().contains("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }

        // If not logged in, redirect to login page
        if (user == null && !request.getRequestURI().endsWith("/login") && !request.getRequestURI().endsWith("/register")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
