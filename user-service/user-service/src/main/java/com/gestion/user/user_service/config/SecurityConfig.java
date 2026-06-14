package com.gestion.user.user_service.config;

import com.gestion.user.user_service.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()

                        // Rutas de Usuarios
                        //.requestMatchers("/api/users/**").hasRole("ADMIN")

                        // Rutas de Pacientes
                        //.requestMatchers(HttpMethod.POST, "/api/patients").hasAnyRole("ADMIN", "PACIENTE")
                        //.requestMatchers(HttpMethod.GET, "/api/patients").hasRole("ADMIN")
                        //.requestMatchers(HttpMethod.GET, "/api/patients/**").hasAnyRole("ADMIN", "MEDICO", "PACIENTE")

                        // Rutas de Doctores
                        //.requestMatchers(HttpMethod.POST, "/api/doctors").hasRole("ADMIN")
                        //.requestMatchers(HttpMethod.GET, "/api/doctors").hasAnyRole("ADMIN", "PACIENTE")
                        //.requestMatchers(HttpMethod.GET, "/api/doctors/**").hasAnyRole("ADMIN", "PACIENTE", "MEDICO")

                        //.anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
