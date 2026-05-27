CREATE SCHEMA IF NOT EXISTS auth_service;

CREATE TABLE IF NOT EXISTS auth_service.roles (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS auth_service.credenciales (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    correo VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol_id BIGINT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_credencial_rol
        FOREIGN KEY (rol_id)
        REFERENCES auth_service.roles(id)
);

CREATE INDEX IF NOT EXISTS idx_credenciales_user_id
ON auth_service.credenciales(user_id);

CREATE INDEX IF NOT EXISTS idx_credenciales_correo
ON auth_service.credenciales(correo);

INSERT INTO auth_service.roles (nombre)
VALUES
('ADMIN'),
('PACIENTE'),
('MEDICO')
ON CONFLICT (nombre) DO NOTHING;