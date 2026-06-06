package com.gestion.auth.auth_service.security;

import com.gestion.auth.auth_service.auth.dto.CrearCredencialRequest;
import com.gestion.auth.auth_service.credential.repository.CredencialRepository;
import com.gestion.auth.auth_service.credential.service.CredencialService;
import com.gestion.auth.auth_service.role.Rol;
import com.gestion.auth.auth_service.role.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final CredencialService credencialService;

    private static final Long USER_ID_ADMIN = 1L;
    private static final String CORREO_ADMIN = "admin@email.com";
    private static final String PASSWORD_ADMIN = "admin123";
    private static final String ROL_ADMIN = "ADMINISTRADOR";

    @Override
    public void run(String... args) {

        Rol rolAdmin = rolRepository.findByNombre(ROL_ADMIN)
                .orElseGet(() -> rolRepository.save(
                        Rol.builder()
                                .nombre(ROL_ADMIN)
                                .build()
                ));

        if (!credencialService.existsByCorreo(CORREO_ADMIN)) {
            credencialService.crearAdminInicial(
                    USER_ID_ADMIN,
                    CORREO_ADMIN,
                    PASSWORD_ADMIN,
                    rolAdmin
            );
        }

        System.out.println("Datos Iniciales Creados: Rol ADMINISTRADOR y Usuario");
        System.out.println("User ID: " + USER_ID_ADMIN);
        System.out.println("Correo: " + CORREO_ADMIN);
        System.out.println("Password: " + PASSWORD_ADMIN);
        System.out.println("Rol: " + ROL_ADMIN);
    }
}