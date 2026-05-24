-- =========================================
-- TABLA: ROL
-- =========================================
CREATE TABLE rol (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

-- =========================================
-- TABLA: USUARIO
-- =========================================
CREATE TABLE usuario (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    rol_id BIGINT,

    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (rol_id)
        REFERENCES rol(id)
);

-- =========================================
-- TABLA: ESPECIALIDAD
-- =========================================
CREATE TABLE especialidad (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

-- =========================================
-- TABLA: PACIENTE
-- =========================================
CREATE TABLE paciente (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    dni VARCHAR(20) NOT NULL UNIQUE,
    fecha_nacimiento DATE NOT NULL,
    direccion VARCHAR(255),

    CONSTRAINT fk_paciente_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id)
);

-- =========================================
-- TABLA: DOCTOR
-- =========================================
CREATE TABLE doctor (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    especialidad_id BIGINT NOT NULL,
    cmp VARCHAR(50) NOT NULL UNIQUE,
    disponible BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_doctor_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id),

    CONSTRAINT fk_doctor_especialidad
        FOREIGN KEY (especialidad_id)
        REFERENCES especialidad(id)
);

-- =========================================
-- TABLA: HORARIO_DOCTOR
-- =========================================
CREATE TABLE horario_doctor (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,

    CONSTRAINT fk_horario_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctor(id)
);

-- =========================================
-- TABLA: CITA
-- =========================================
CREATE TABLE cita (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    motivo VARCHAR(255),
    estado VARCHAR(50) NOT NULL,

    CONSTRAINT fk_cita_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente(id),

    CONSTRAINT fk_cita_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctor(id)
);

-- =========================================
-- TABLA: REPORTE
-- =========================================
CREATE TABLE reporte (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cita_id BIGINT NOT NULL,
    descripcion TEXT NOT NULL,
    fecha_generacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reporte_cita
        FOREIGN KEY (cita_id)
        REFERENCES cita(id)
);

-- =========================================
-- INSERTS INICIALES
-- =========================================

INSERT INTO rol (nombre) VALUES
('PACIENTE'),
('MEDICO');

-- Especialidades base
INSERT INTO especialidad (nombre, descripcion) VALUES
('Cardiología', 'Especialista en corazón'),
('Pediatría', 'Especialista en niños'),
('Dermatología', 'Especialista en piel');
