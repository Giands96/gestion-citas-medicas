package com.gestion.user.user_service.bootstrap;

import com.gestion.user.user_service.credential.CredencialEntity;
import com.gestion.user.user_service.credential.CredencialRepository;
import com.gestion.user.user_service.credential.RolEntity;
import com.gestion.user.user_service.credential.RolRepository;
import com.gestion.user.user_service.entity.Usuario;
import com.gestion.user.user_service.enums.Rol;
import com.gestion.user.user_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final CredencialRepository credencialRepository;
    private final RolRepository rolRepository;

    @Override
    public void run(String... args) {
        if (credencialRepository.existsByCorreo("admin@clinica.com")) {
            log.info("Admin user already exists, skipping seed.");
            return;
        }

        Usuario admin = Usuario.builder()
                .nombres("Admin")
                .apellidos("Principal")
                .rol(Rol.ADMIN)
                .activo(true)
                .build();
        Usuario guardado = usuarioRepository.save(admin);
        log.info("Admin created with ID {}", guardado.getId());

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
        log.info("Admin credentials created");
    }
}
