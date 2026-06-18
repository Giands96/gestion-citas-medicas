# Flujo de API - Gestión de Citas Médicas

Todos los endpoints pasan por el API Gateway en `http://localhost:8080`.

---

## 1. Autenticación (Admin por defecto)

```bash
# Login como admin (creado automáticamente al iniciar el sistema)
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "admin@clinica.com",
    "password": "admin123"
  }' | jq
```

**Respuesta:** Obtienes un `token` JWT. Guarda ese token para los siguientes requests.

```bash
# Guardar token en variable
TOKEN="<token_del_response>"
```

---

## 2. Especialidades Médicas

```bash
# Crear especialidad
curl -s -X POST http://localhost:8080/api/medicals/especialidades \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombre": "Cardiología",
    "descripcion": "Especialista en el corazón"
  }' | jq

# Crear más especialidades
curl -s -X POST http://localhost:8080/api/medicals/especialidades \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombre": "Pediatría",
    "descripcion": "Especialista en niños"
  }' | jq

curl -s -X POST http://localhost:8080/api/medicals/especialidades \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombre": "Dermatología",
    "descripcion": "Especialista en la piel"
  }' | jq

# Listar especialidades
curl -s http://localhost:8080/api/medicals/especialidades \
  -H "Authorization: Bearer $TOKEN" | jq

# Guardar ID de especialidad (ej: Cardiología = 1)
ESPECIALIDAD_ID=1
```

---

## 3. Usuarios (Pacientes y Médicos)

### Crear paciente

```bash
curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombres": "Juan",
    "apellidos": "Pérez López",
    "telefono": "999888777",
    "direccion": "Av. Principal 123",
    "fechaNacimiento": "1990-05-15",
    "rol": "PACIENTE",
    "correo": "juan.perez@email.com",
    "password": "password123"
  }' | jq

# Guardar ID del paciente
PACIENTE_ID=1
```

### Crear médico

```bash
curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombres": "María",
    "apellidos": "García Torres",
    "telefono": "999111222",
    "direccion": "Av. Secundaria 456",
    "fechaNacimiento": "1985-08-20",
    "rol": "MEDICO",
    "correo": "maria.garcia@email.com",
    "password": "password123"
  }' | jq

# Guardar ID del médico (usuario)
MEDICO_USER_ID=2
```

### Listar usuarios

```bash
curl -s http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN" | jq
```

---

## 4. Doctores (Perfil médico con especialidad)

```bash
# Crear perfil de doctor vinculado al usuario médico
curl -s -X POST http://localhost:8080/api/medicals/doctores \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "usuarioId": '$MEDICO_USER_ID',
    "especialidadId": '$ESPECIALIDAD_ID',
    "cmp": "CMP-12345",
    "disponible": true
  }' | jq

# Guardar ID del doctor
DOCTOR_ID=1

# Listar doctores
curl -s http://localhost:8080/api/medicals/doctores \
  -H "Authorization: Bearer $TOKEN" | jq

# Obtener doctor por ID
curl -s http://localhost:8080/api/medicals/doctores/$DOCTOR_ID \
  -H "Authorization: Bearer $TOKEN" | jq

# Listar doctores por especialidad
curl -s http://localhost:8080/api/medicals/doctores/especialidad/$ESPECIALIDAD_ID \
  -H "Authorization: Bearer $TOKEN" | jq
```

---

## 5. Citas (Appointments)

```bash
# Crear cita
curl -s -X POST http://localhost:8080/api/citas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "pacienteId": '$PACIENTE_ID',
    "doctorId": '$DOCTOR_ID',
    "fecha": "2026-06-18",
    "hora": "10:30:00",
    "motivo": "Consulta general de control"
  }' | jq

# Guardar ID de la cita
CITA_ID=1

# Listar citas
curl -s http://localhost:8080/api/citas \
  -H "Authorization: Bearer $TOKEN" | jq

# Obtener cita por ID
curl -s http://localhost:8080/api/citas/$CITA_ID \
  -H "Authorization: Bearer $TOKEN" | jq

# Actualizar cita (ej: cambiar estado a CONFIRMADA)
curl -s -X PUT http://localhost:8080/api/citas/$CITA_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "pacienteId": '$PACIENTE_ID',
    "doctorId": '$DOCTOR_ID',
    "fecha": "2026-06-18",
    "hora": "10:30:00",
    "motivo": "Consulta general de control",
    "estado": "CONFIRMADA"
  }' | jq

# Eliminar cita
curl -s -X DELETE http://localhost:8080/api/citas/$CITA_ID \
  -H "Authorization: Bearer $TOKEN"
```

---

## 6. Actualizar y Eliminar Usuarios

```bash
# Actualizar usuario
curl -s -X PUT http://localhost:8080/api/users/$PACIENTE_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombres": "Juan Carlos",
    "apellidos": "Pérez López",
    "telefono": "999888777",
    "rol": "PACIENTE",
    "correo": "juan.perez@email.com",
    "password": "nuevopassword123"
  }' | jq

# Eliminar usuario (desactivación lógica)
curl -s -X DELETE http://localhost:8080/api/users/$PACIENTE_ID \
  -H "Authorization: Bearer $TOKEN"
```

---

## 7. Actualizar y Eliminar Doctores

```bash
# Actualizar doctor
curl -s -X PUT http://localhost:8080/api/medicals/doctores/$DOCTOR_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "usuarioId": '$MEDICO_USER_ID',
    "especialidadId": 2,
    "cmp": "CMP-54321",
    "disponible": false
  }' | jq

# Eliminar doctor
curl -s -X DELETE http://localhost:8080/api/medicals/doctores/$DOCTOR_ID \
  -H "Authorization: Bearer $TOKEN"
```

---

## 8. Actualizar y Eliminar Especialidades

```bash
# Actualizar especialidad
curl -s -X PUT http://localhost:8080/api/medicals/especialidades/$ESPECIALIDAD_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombre": "Cardiología Clínica",
    "descripcion": "Especialista en el corazón y sistema circulatorio"
  }' | jq

# Eliminar especialidad
curl -s -X DELETE http://localhost:8080/api/medicals/especialidades/$ESPECIALIDAD_ID \
  -H "Authorization: Bearer $TOKEN"
```

---

## Resumen de Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/auth/login` | Login (obtener JWT) |
| `GET` | `/api/users` | Listar usuarios |
| `POST` | `/api/users` | Crear usuario |
| `GET` | `/api/users/{id}` | Obtener usuario |
| `PUT` | `/api/users/{id}` | Actualizar usuario |
| `DELETE` | `/api/users/{id}` | Eliminar usuario |
| `GET` | `/api/medicals/especialidades` | Listar especialidades |
| `POST` | `/api/medicals/especialidades` | Crear especialidad |
| `GET` | `/api/medicals/especialidades/{id}` | Obtener especialidad |
| `PUT` | `/api/medicals/especialidades/{id}` | Actualizar especialidad |
| `DELETE` | `/api/medicals/especialidades/{id}` | Eliminar especialidad |
| `GET` | `/api/medicals/doctores` | Listar doctores |
| `POST` | `/api/medicals/doctores` | Crear doctor |
| `GET` | `/api/medicals/doctores/{id}` | Obtener doctor |
| `GET` | `/api/medicals/doctores/especialidad/{especialidadId}` | Doctores por especialidad |
| `PUT` | `/api/medicals/doctores/{id}` | Actualizar doctor |
| `DELETE` | `/api/medicals/doctores/{id}` | Eliminar doctor |
| `GET` | `/api/citas` | Listar citas |
| `POST` | `/api/citas` | Crear cita |
| `GET` | `/api/citas/{id}` | Obtener cita |
| `PUT` | `/api/citas/{id}` | Actualizar cita |
| `DELETE` | `/api/citas/{id}` | Eliminar cita |
