# Flujo de pruebas con CURL / Postman

> Todos los servicios se ejecutan localmente. Ajusta IP/puertos si usas Docker.

---

## 1. Crear usuario (ADMIN) + credenciales

```bash
curl -s -X POST http://localhost:8082/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "nombres": "Admin",
    "apellidos": "Sistema",
    "telefono": "999999999",
    "direccion": "Av. Principal 123",
    "fechaNacimiento": "1990-01-01",
    "rol": "ADMIN",
    "correo": "admin@clinica.com",
    "password": "admin123"
  }' | jq
```

## 2. Login (obtener token JWT)

```bash
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "admin@clinica.com",
    "password": "admin123"
  }' | jq -r '.token')

echo $TOKEN
```

## 3. Crear paciente

```bash
RESP=$(curl -s -X POST http://localhost:8082/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombres": "Juan",
    "apellidos": "Perez Lopez",
    "telefono": "987654321",
    "direccion": "Jr. Las Flores 456",
    "fechaNacimiento": "1985-05-15",
    "rol": "PACIENTE",
    "correo": "juan@email.com",
    "password": "password123"
  }')

echo $RESP | jq

# Capturar dinámicamente el ID del paciente
PACIENTE_ID=$(echo $RESP | jq -r '.id')
echo "PACIENTE_ID=$PACIENTE_ID"
```

## 4. Crear doctor (MEDICO) + credenciales

```bash
RESP=$(curl -s -X POST http://localhost:8082/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombres": "Maria",
    "apellidos": "Garcia Torres",
    "telefono": "987651234",
    "direccion": "Av. Salud 789",
    "fechaNacimiento": "1980-10-20",
    "rol": "MEDICO",
    "correo": "maria@clinica.com",
    "password": "password123"
  }')

echo $RESP | jq

# Capturar dinámicamente el ID del usuario médico
MEDICO_USER_ID=$(echo $RESP | jq -r '.id')
echo "MEDICO_USER_ID=$MEDICO_USER_ID"
```

> **⚠️ Importante:** El `MEDICO_USER_ID` se obtiene dinámicamente de la respuesta. No uses números fijos; depende del orden de creación.

## 5. Listar especialidades (ya existen 3 precargadas)

```bash
curl -s http://localhost:8084/api/medicals/especialidades | jq
```

**Respuesta esperada:**
```json
[
  { "id": 1, "nombre": "Cardiologia", "descripcion": "Especialista en corazon", "activa": true },
  { "id": 2, "nombre": "Pediatria", "descripcion": "Especialista en ninos", "activa": true },
  { "id": 3, "nombre": "Dermatologia", "descripcion": "Especialista en piel", "activa": true }
]
```

## 6. Registrar al doctor en medical-service

```bash
RESP=$(curl -s -X POST http://localhost:8084/api/medicals/doctores \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": '"$MEDICO_USER_ID"',
    "especialidadId": 1,
    "cmp": "CMP-12345"
  }')

echo $RESP | jq

# Capturar dinámicamente el ID del doctor en medical
DOCTOR_ID=$(echo $RESP | jq -r '.id')
echo "DOCTOR_ID=$DOCTOR_ID"
```

## 7. Obtener doctores por especialidad

```bash
curl -s http://localhost:8084/api/medicals/doctores/especialidad/1 | jq
```

## 8. Crear una cita (autenticado)

```bash
curl -s -X POST http://localhost:8083/api/citas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "pacienteId": '"$PACIENTE_ID"',
    "doctorId": '"$DOCTOR_ID"',
    "fecha": "2026-06-20",
    "hora": "10:30",
    "motivo": "Control cardiaco de rutina"
  }' | jq
```

**Respuesta esperada (enriquecida):**
```json
{
  "id": 1,
  "pacienteId": 2,
  "pacienteNombre": "Juan",
  "pacienteApellido": "Perez Lopez",
  "doctorId": 1,
  "doctorNombre": "Maria",
  "doctorApellido": "Garcia Torres",
  "especialidadId": 1,
  "especialidadNombre": "Cardiologia",
  "fecha": "2026-06-20",
  "hora": "10:30:00",
  "motivo": "Control cardiaco de rutina",
  "estado": "PENDIENTE"
}
```

> Si `doctorNombre` y `doctorApellido` salen `"N/A"`, significa que el `usuarioId` registrado en medical-service no coincide con ningún usuario en user-service. Revisa el paso 6.

## 9. Listar todas las citas

```bash
curl -s http://localhost:8083/api/citas \
  -H "Authorization: Bearer $TOKEN" | jq
```

## 10. Obtener cita por ID

```bash
curl -s http://localhost:8083/api/citas/1 \
  -H "Authorization: Bearer $TOKEN" | jq
```

## 11. Actualizar cita

```bash
curl -s -X PUT http://localhost:8083/api/citas/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "pacienteId": '"$PACIENTE_ID"',
    "doctorId": '"$DOCTOR_ID"',
    "fecha": "2026-06-21",
    "hora": "11:00",
    "motivo": "Control reprogramado"
  }' | jq
```

## 12. Eliminar cita

```bash
curl -s -X DELETE http://localhost:8083/api/citas/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## Resumen de puertos

| Servicio | Puerto |
|---|---|
| Eureka Server | 8761 |
| API Gateway | 8080 |
| Auth Service | 8081 |
| User Service | 8082 |
| Cita Service | 8083 |
| Medical Service | 8084 |

> **Nota:** Los endpoints también funcionan a través del API Gateway (`http://localhost:8080/api/citas`, `http://localhost:8080/api/medicals/...`, etc.) ya que el gateway enruta automáticamente.
