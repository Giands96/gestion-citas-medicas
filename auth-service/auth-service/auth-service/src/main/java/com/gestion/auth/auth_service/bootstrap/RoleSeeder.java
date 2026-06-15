package com.gestion.auth.auth_service.bootstrap;

import com.gestion.auth.auth_service.role.Rol;
import com.gestion.auth.auth_service.role.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;

    @Override
    public void run(String... args) {
        List<String> roles = List.of("ADMIN", "PACIENTE", "MEDICO");

        for (String nombre : roles) {
            if (rolRepository.findByNombre(nombre).isEmpty()) {
                rolRepository.save(Rol.builder().nombre(nombre).build());
                log.info("Rol '{}' creado", nombre);
            }
        }
    }
}
