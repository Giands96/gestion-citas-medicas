package com.gestion.user.user_service;

import com.gestion.user.user_service.credential.CredencialEntity;
import com.gestion.user.user_service.credential.CredencialRepository;
import com.gestion.user.user_service.credential.RolEntity;
import com.gestion.user.user_service.credential.RolRepository;
import com.gestion.user.user_service.entity.Usuario;
import com.gestion.user.user_service.enums.Rol;
import com.gestion.user.user_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
public class UserServiceApplication {

    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedAdmin(UsuarioRepository usuarioRepository,
                                       CredencialRepository credencialRepository,
                                       RolRepository rolRepository) {
        return args -> {
            if (credencialRepository.existsByCorreo("admin@clinica.com")) {
                return;
            }

            Usuario admin = Usuario.builder()
                    .nombres("Admin")
                    .apellidos("Principal")
                    .rol(Rol.ADMIN)
                    .activo(true)
                    .build();
            Usuario guardado = usuarioRepository.save(admin);
            log.info("Admin creado con ID {}", guardado.getId());

            RolEntity rol = rolRepository.findByNombre("ADMIN")
                    .orElseGet(() -> rolRepository.save(
                            RolEntity.builder().nombre("ADMIN").build()
                    ));

            CredencialEntity credencial = CredencialEntity.builder()
                    .userId(guardado.getId())
                    .correo("admin@clinica.com")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(rol)
                    .activo(true)
                    .build();
            credencialRepository.save(credencial);
            log.info("Credenciales de admin creadas");
        };
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserServiceApplication.class);
}
