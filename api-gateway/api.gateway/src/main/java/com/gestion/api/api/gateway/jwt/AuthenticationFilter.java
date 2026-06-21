package com.gestion.api.api.gateway.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    @Value("${internal.gateway.secret}")
    private String internalGatewaySecret;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final List<String> publicPaths = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register"
    );

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (publicPaths.stream().anyMatch(p -> pathMatcher.match(p, path))) {
            ServerHttpRequest mutatedRequest = addInternalHeaders(request, null);
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        String token = extractToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Token invalido o ausente");
        }

        Claims claims = jwtUtil.getClaims(token);
        String subject = claims.getSubject();
        Object userId = claims.get("userId");
        String role = claims.get("role", String.class);

        if (subject == null || userId == null || role == null) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Claims de usuario ausentes");
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        subject,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

        ServerHttpRequest mutatedRequest = addInternalHeaders(request, claims);
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        return chain.filter(mutatedExchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"status\":%d,\"error\":\"%s\"}", status.value(), message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private ServerHttpRequest addInternalHeaders(ServerHttpRequest request, Claims claims) {
        return request.mutate()
                .headers(headers -> {
                    headers.set("X-Internal-Request", internalGatewaySecret);
                    headers.remove("X-User-Email");
                    headers.remove("X-User-Id");
                    headers.remove("X-User-Role");

                    if (claims != null) {
                        headers.set("X-User-Email", claims.getSubject());
                        headers.set("X-User-Id", String.valueOf(claims.get("userId")));
                        headers.set("X-User-Role", claims.get("role", String.class));
                    }
                })
                .build();
    }
}
