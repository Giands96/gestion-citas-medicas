package com.gestion.auth.auth_service.security;

import com.gestion.auth.auth_service.credential.entity.Credencial;
import com.gestion.auth.auth_service.credential.repository.CredencialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CredencialRepository credentialRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Credencial credencial = credentialRepository.findByCorreo(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Credenciales no encontradas")//* No mencionar el correo para evitar dar pistas a posibles atacantes
                );

        return new CustomUserDetails(credencial);
    }
}