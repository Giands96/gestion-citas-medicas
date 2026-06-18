package com.gestion.user.user_service.credential;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<RolEntity, Long> {
    Optional<RolEntity> findByNombre(String nombre);
}
