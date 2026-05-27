-- ============================================================
-- INIT SCRIPT - SISTEMA GESTIÓN MÉDICA CON MICROSERVICIOS
-- PostgreSQL
-- Enfoque académico:
-- - Una sola base de datos física: clinica_db
-- - Un schema por microservicio
-- - Sin foreign keys entre schemas/microservicios
-- - Cada servicio es dueño de sus propias tablas
-- ============================================================

-- ============================================================
-- SCHEMAS POR MICROSERVICIO
-- ============================================================
CREATE SCHEMA IF NOT EXISTS auth_service;
CREATE SCHEMA IF NOT EXISTS patient_service;
CREATE SCHEMA IF NOT EXISTS doctor_service;
CREATE SCHEMA IF NOT EXISTS appointment_service;
CREATE SCHEMA IF NOT EXISTS report_service;
CREATE SCHEMA IF NOT EXISTS notification_service;

-- ============================================================
-- AUTH SERVICE
-- Responsable de autenticación, usuarios base, roles y credenciales.
-- Aquí SÍ puede existir FK entre usuario y rol porque ambas tablas
-- pertenecen al mismo microservicio/schema.
-- ============================================================

CREATE TABLE IF NOT EXISTS auth_service.roles (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS auth_service.usuarios (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    rol_id BIGINT NOT NULL,

    CONSTRAINT fk_auth_usuario_rol
        FOREIGN KEY (rol_id)
        REFERENCES auth_service.roles(id)
);

-- ============================================================
-- PATIENT SERVICE
-- Responsable de los datos clínicos/básicos del paciente.
-- usuario_id referencia lógica hacia auth_service.usuarios.id,
-- pero NO tiene FK física porque pertenece a otro microservicio.
-- ============================================================

CREATE TABLE IF NOT EXISTS patient_service.pacientes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    dni VARCHAR(20) NOT NULL UNIQUE,
    fecha_nacimiento DATE NOT NULL,
    direccion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_pacientes_usuario_id
ON patient_service.pacientes(usuario_id);

-- ============================================================
-- DOCTOR SERVICE
-- Responsable de doctores, especialidades y horarios.
-- usuario_id referencia lógica hacia auth_service.usuarios.id,
-- pero NO tiene FK física.
-- especialidad_id SÍ puede tener FK porque especialidades pertenece
-- al mismo schema/microservicio.
-- ============================================================

CREATE TABLE IF NOT EXISTS doctor_service.especialidades (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activa BOOLEAN NOT NULL DEFAULT TRUE
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

    CONSTRAINT fk_horario_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctor_service.doctores(id)
);

-- ============================================================
-- APPOINTMENT SERVICE
-- Responsable de citas.
-- paciente_id y doctor_id son referencias lógicas hacia otros
-- microservicios. NO se usan FK físicas.
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
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_citas_paciente_id
ON appointment_service.citas(paciente_id);

CREATE INDEX IF NOT EXISTS idx_citas_doctor_id
ON appointment_service.citas(doctor_id);

CREATE INDEX IF NOT EXISTS idx_citas_fecha_hora
ON appointment_service.citas(fecha, hora);

-- Evita doble reserva del mismo doctor a la misma fecha/hora.
ALTER TABLE appointment_service.citas
ADD CONSTRAINT uk_citas_doctor_fecha_hora
UNIQUE (doctor_id, fecha, hora);

-- ============================================================
-- REPORT SERVICE
-- Responsable de reportes/historial de atención.
-- cita_id es referencia lógica hacia appointment_service.citas.id.
-- NO se usa FK física.
-- ============================================================

CREATE TABLE IF NOT EXISTS report_service.reportes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cita_id BIGINT NOT NULL,
    paciente_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    descripcion TEXT NOT NULL,
    fecha_generacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_reportes_cita_id
ON report_service.reportes(cita_id);

CREATE INDEX IF NOT EXISTS idx_reportes_paciente_id
ON report_service.reportes(paciente_id);

-- ============================================================
-- NOTIFICATION SERVICE
-- Responsable de notificaciones enviadas o pendientes.
-- usuario_id es referencia lógica hacia auth_service.usuarios.id.
-- ============================================================

CREATE TABLE IF NOT EXISTS notification_service.notificaciones (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    canal VARCHAR(50) NOT NULL,
    asunto VARCHAR(150),
    mensaje TEXT NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP
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
