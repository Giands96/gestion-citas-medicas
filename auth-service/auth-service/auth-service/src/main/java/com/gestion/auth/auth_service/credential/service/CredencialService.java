package com.gestion.auth.auth_service.credential.service;

import com.gestion.auth.auth_service.credential.repository.CredencialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CredencialService {

    private final CredencialRepository credencialRepository;

    public boolean existsByCorreo(String correo) {
        return credencialRepository.existsByCorreo(correo);
    }

}
