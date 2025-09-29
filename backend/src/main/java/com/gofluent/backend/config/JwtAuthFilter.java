package com.gofluent.backend.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserAuthProvider userAuthProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // 🔍 DEBUG LOG
        System.out.println("🔍 JWT Filter - Path: " + path + ", Method: " + method);

        if (path.equals("/login") || path.equals("/register")) {
            System.out.println("✅ Skipping JWT filter for: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("🛡️ JWT Filter processing: " + path);

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Authentication auth = userAuthProvider.validateToken(token);

                // 👇 LOG ce user și ce roluri au intrat în SecurityContext
                System.out.println("✅ JWT validat pentru user: " + auth.getName());
                System.out.println("   Authorities: " + auth.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (RuntimeException e) {
                System.out.println("❌ JWT validation failed: " + e.getMessage());
                SecurityContextHolder.clearContext();
                throw e;
            }
        }

        filterChain.doFilter(request, response);
    }

}