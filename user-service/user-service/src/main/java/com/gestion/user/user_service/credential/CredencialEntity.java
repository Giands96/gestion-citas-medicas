package com.gestion.user.user_service.credential;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "credenciales", schema = "auth_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CredencialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(nullable = false, length = 255)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private RolEntity rol;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
