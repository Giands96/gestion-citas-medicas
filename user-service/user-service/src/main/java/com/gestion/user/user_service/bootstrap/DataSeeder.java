package com.gestion.user.user_service.bootstrap;

import com.gestion.user.user_service.client.AuthServiceClient;
import com.gestion.user.user_service.client.dto.CredencialRequest;
import com.gestion.user.user_service.entity.Usuario;
import com.gestion.user.user_service.enums.Rol;
import com.gestion.user.user_service.repository.UsuarioRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final AuthServiceClient authServiceClient;

    private static final String ADMIN_NOMBRES = "Admin";
    private static final String ADMIN_APELLIDOS = "Sistema";
    private static final String ADMIN_CORREO = "admin@email.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByNombres(ADMIN_NOMBRES).isPresent()) {
            log.info("Admin user already exists, skipping bootstrap");
            return;
        }

        Usuario admin = Usuario.builder()
                .nombres(ADMIN_NOMBRES)
                .apellidos(ADMIN_APELLIDOS)
                .rol(Rol.ADMIN)
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(admin);

        try {
            CredencialRequest credencialRequest = new CredencialRequest(
                    guardado.getId(),
                    ADMIN_CORREO,
                    ADMIN_PASSWORD,
                    Rol.ADMIN.name()
            );
            authServiceClient.createCredential(credencialRequest);
            log.info("USUARIO ADMIN CREADO: userId={}, email={}", guardado.getId(), ADMIN_CORREO);
        } catch (FeignException e) {
            log.error("ERROR AL CREAR CREDENCIAL: {}", e.getMessage());
            usuarioRepository.delete(guardado);
        }
    }
}
