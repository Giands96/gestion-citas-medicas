package com.gestion.user.user_service.credential;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CredencialRepository extends JpaRepository<CredencialEntity, Long> {
    boolean existsByCorreo(String correo);
    Optional<CredencialEntity> findByCorreo(String correo);
}
