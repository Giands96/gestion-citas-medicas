package com.gestion.cita.cita_service.client;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Configuration
public class FeignHeaderConfig {

    private static final List<String> INTERNAL_HEADERS = List.of(
            "X-Internal-Request",
            "X-User-Email",
            "X-User-Id",
            "X-User-Role"
    );

    @Bean
    public RequestInterceptor internalHeadersRequestInterceptor() {
        return template -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return;
            }

            HttpServletRequest currentRequest = attributes.getRequest();

            for (String headerName : INTERNAL_HEADERS) {
                String headerValue = currentRequest.getHeader(headerName);
                if (headerValue != null && !headerValue.isBlank()) {
                    template.header(headerName, headerValue);
                }
            }
        };
    }
}
