-- ============================================================
-- SCHEMAS POR MICROSERVICIO
-- ============================================================

CREATE SCHEMA IF NOT EXISTS auth_service;
CREATE SCHEMA IF NOT EXISTS user_service;
CREATE SCHEMA IF NOT EXISTS patient_service;
CREATE SCHEMA IF NOT EXISTS doctor_service;
CREATE SCHEMA IF NOT EXISTS appointment_service;
CREATE SCHEMA IF NOT EXISTS report_service;
CREATE SCHEMA IF NOT EXISTS notification_service;

-- ============================================================
-- USER SERVICE
-- Responsable de perfiles generales de usuarios.
-- ============================================================

CREATE TABLE IF NOT EXISTS user_service.usuarios (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    fecha_nacimiento DATE,
    tipo_usuario VARCHAR(50) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT chk_tipo_usuario
        CHECK (tipo_usuario IN ('ADMIN', 'PACIENTE', 'MEDICO'))
);

-- ============================================================
-- AUTH SERVICE
-- Responsable de login, credenciales, roles y estado de acceso.
-- user_id referencia lógica hacia user_service.usuarios.id.
-- NO se usa FK física porque pertenece a otro microservicio.
-- ============================================================

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

-- ============================================================
-- PATIENT SERVICE
-- Responsable de información propia del paciente.
-- usuario_id referencia lógica hacia user_service.usuarios.id.
-- ============================================================

CREATE TABLE IF NOT EXISTS patient_service.pacientes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    dni VARCHAR(20) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_pacientes_usuario_id
ON patient_service.pacientes(usuario_id);

-- ============================================================
-- DOCTOR SERVICE
-- Responsable de médicos, especialidades y horarios.
-- usuario_id referencia lógica hacia user_service.usuarios.id.
-- ============================================================

CREATE TABLE IF NOT EXISTS doctor_service.especialidades (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS doctor_service.doctores (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    especialidad_id BIGINT NOT NULL,
    cmp VARCHAR(50) NOT NULL UNIQUE,
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_doctor_especialidad
        FOREIGN KEY (especialidad_id)
        REFERENCES doctor_service.especialidades(id)
);

CREATE INDEX IF NOT EXISTS idx_doctores_usuario_id
ON doctor_service.doctores(usuario_id);

CREATE TABLE IF NOT EXISTS doctor_service.horarios_doctor (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_horario_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctor_service.doctores(id),

    CONSTRAINT chk_dia_semana
        CHECK (dia_semana IN (
            'LUNES', 'MARTES', 'MIERCOLES', 'JUEVES',
            'VIERNES', 'SABADO', 'DOMINGO'
        )),

    CONSTRAINT chk_horario_valido
        CHECK (hora_inicio < hora_fin)
);

-- ============================================================
-- APPOINTMENT SERVICE
-- Responsable de citas.
-- paciente_id y doctor_id son referencias lógicas.
-- ============================================================

CREATE TABLE IF NOT EXISTS appointment_service.citas (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    motivo VARCHAR(255),
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT chk_estado_cita
        CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'ATENDIDA')),

    CONSTRAINT uk_citas_doctor_fecha_hora
        UNIQUE (doctor_id, fecha, hora)
);

CREATE INDEX IF NOT EXISTS idx_citas_paciente_id
ON appointment_service.citas(paciente_id);

CREATE INDEX IF NOT EXISTS idx_citas_doctor_id
ON appointment_service.citas(doctor_id);

CREATE INDEX IF NOT EXISTS idx_citas_fecha_hora
ON appointment_service.citas(fecha, hora);

-- ============================================================
-- REPORT SERVICE
-- Responsable de reportes médicos.
-- cita_id, paciente_id y doctor_id son referencias lógicas.
-- ============================================================

CREATE TABLE IF NOT EXISTS report_service.reportes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cita_id BIGINT NOT NULL,
    paciente_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    diagnostico TEXT,
    tratamiento TEXT,
    observaciones TEXT,
    fecha_generacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_reportes_cita_id
ON report_service.reportes(cita_id);

CREATE INDEX IF NOT EXISTS idx_reportes_paciente_id
ON report_service.reportes(paciente_id);

CREATE INDEX IF NOT EXISTS idx_reportes_doctor_id
ON report_service.reportes(doctor_id);

-- ============================================================
-- NOTIFICATION SERVICE
-- Responsable de notificaciones.
-- usuario_id referencia lógica hacia user_service.usuarios.id.
-- ============================================================

CREATE TABLE IF NOT EXISTS notification_service.notificaciones (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    canal VARCHAR(50) NOT NULL,
    asunto VARCHAR(150),
    mensaje TEXT NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    intentos INT NOT NULL DEFAULT 0,
    error_mensaje TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP,

    CONSTRAINT chk_canal_notificacion
        CHECK (canal IN ('EMAIL', 'SMS', 'WHATSAPP')),

    CONSTRAINT chk_estado_notificacion
        CHECK (estado IN ('PENDIENTE', 'ENVIADA', 'FALLIDA'))
);

CREATE INDEX IF NOT EXISTS idx_notificaciones_usuario_id
ON notification_service.notificaciones(usuario_id);

-- ============================================================
-- DATA INICIAL
-- ============================================================

INSERT INTO auth_service.roles (nombre)
VALUES
('ADMIN'),
('PACIENTE'),
('MEDICO')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO doctor_service.especialidades (nombre, descripcion)
VALUES
('Cardiología', 'Especialista en corazón'),
('Pediatría', 'Especialista en niños'),
('Dermatología', 'Especialista en piel')
ON CONFLICT (nombre) DO NOTHING;