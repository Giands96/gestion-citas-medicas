package com.gestion.user.user_service.security;

import com.gestion.user.user_service.entity.Usuario;
import com.gestion.user.user_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca a tu usuario en tu BD
        Usuario usuario = usuarioRepository.findByNombres(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Retorna un objeto User de Spring Security con los roles
        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getNombres())
                .password("") // En el user-service no necesitas la contraseña
                .roles(usuario.getTipoUsuario().name()) // Convierte tu Enum a String
                .build();
    }
}
