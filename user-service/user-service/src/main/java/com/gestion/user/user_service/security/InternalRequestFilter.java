package com.gestion.user.user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(InternalRequestFilter.class);

    @Value("${internal.gateway.secret}")
    private String expectedSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String internalHeader = request.getHeader("X-Internal-Request");

        if (!expectedSecret.equals(internalHeader)) {
            log.warn("Acceso directo bloqueado - IP: {} Path: {}",
                    request.getRemoteAddr(), request.getRequestURI());
            sendError(response, HttpStatus.FORBIDDEN, "Acceso directo no permitido");
            return;
        }

        String userId = request.getHeader("X-User-Id");
        String correo = request.getHeader("X-User-Email");
        String role   = request.getHeader("X-User-Role");

        if (userId == null || role == null) {
            sendError(response, HttpStatus.UNAUTHORIZED, "Claims de usuario ausentes");
            return;
        }

        JwtUserDetails userDetails = new JwtUserDetails(
                Long.parseLong(userId), correo, role
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response,
                           HttpStatus status,
                           String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                String.format("{\"status\":%d,\"error\":\"%s\"}", status.value(), message)
        );
    }
}