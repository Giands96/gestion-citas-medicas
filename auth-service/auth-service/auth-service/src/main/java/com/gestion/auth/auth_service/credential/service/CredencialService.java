package com.gestion.auth.auth_service.credential.service;

import com.gestion.auth.auth_service.credential.entity.Credencial;
import com.gestion.auth.auth_service.credential.repository.CredencialRepository;
import com.gestion.auth.auth_service.role.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CredencialService {

    private final CredencialRepository credencialRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean existsByCorreo(String correo) {
        return credencialRepository.existsByCorreo(correo);
    }

    public Credencial crearAdminInicial(Long userId, String correo, String passwordPlano, Rol rol) {

        if (credencialRepository.existsByCorreo(correo)) {
            throw new IllegalStateException("El correo admin ya existe");
        }

        Credencial credencial = Credencial.builder()
                .userId(userId)
                .correo(correo)
                .password(passwordEncoder.encode(passwordPlano))
                .rol(rol)
                .activo(true)
                .build();

        return credencialRepository.save(credencial);
    }
}
