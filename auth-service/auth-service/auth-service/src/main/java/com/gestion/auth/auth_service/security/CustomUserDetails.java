package com.gestion.auth.auth_service.security;

import com.gestion.auth.auth_service.credential.entity.Credencial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Credencial credencial;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + credencial.getRol().getNombre()
                )
        );
    }

    @Override
    public String getPassword() {
        return credencial.getPassword();
    }

    @Override
    public String getUsername() {
        return credencial.getCorreo();
    }

    public Long getCredentialId() {
        return credencial.getId();
    }

    public String getEmail() {
        return credencial.getCorreo();
    }

    public String getRoleName() {
        return credencial.getRol().getNombre();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return credencial.getActivo();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return credencial.getActivo();
    }


}
