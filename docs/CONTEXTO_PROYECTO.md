# Sistema de Gestión de Citas Médicas

## Arquitectura General

Sistema basado en **microservicios** con **Spring Boot 4.0.6**, **Java 21**, **Spring Cloud 2025.1.1** y **PostgreSQL 16**.

```
Cliente → API Gateway (8080) → Microservicios
                                ├── auth-service (8081)
                                ├── user-service (8082)
                                ├── cita-service (8083)
                                ├── medical-service (8084)
                                └── notification-service (8085)
```

---

## 1. API Gateway (`api-gateway/api.gateway`)

**Puerto:** 8080  
**Base package:** `com.gestion.api.api.gateway`

### Dependencias clave
- `spring-cloud-starter-gateway-server-webflux`
- `spring-cloud-starter-netflix-eureka-client`
- `spring-boot-starter-security`
- `jjwt-api/impl/jackson 0.12.6`

### application.yaml
```yaml
server:
  port: 8080
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: http://auth-service:8081
          predicates:
            - Path=/api/auth/**
        - id: user-service
          uri: http://user-service:8082
          predicates:
            - Path=/api/users/**
        - id: cita-service
          uri: http://cita-service:8083
          predicates:
            - Path=/api/citas/**
        - id: medical-service
          uri: http://medical-service:8084
          predicates:
            - Path=/api/medicals/**
        - id: notification-service
          uri: http://notification-service:8085
          predicates:
            - Path=/api/notifications/**
jwt:
  secret: ${JWT_SECRET:Zd2W3jBD8D2xFUSNPywUHDFeuFunANkdKKnaIQzFjbj}
  expiration: ${JWT_EXPIRATION:3600000}
internal:
  gateway:
    secret: ${INTERNAL_GATEWAY_SECRET:5rO2rtldA8350502DreTcvFu9n2s8v7Xo1j6z9w0}
```

### SecurityConfig.java
- `@EnableWebFluxSecurity` (Spring WebFlux Security)
- Permite `/api/auth/login` y `/api/auth/register` sin autenticación
- El resto de rutas requieren autenticación vía `.anyExchange().authenticated()`

### AuthenticationFilter.java (WebFilter)
- Implementa `WebFilter` con `@Order(Ordered.HIGHEST_PRECEDENCE)`
- Usa constructor injection (`JwtUtil`)
- **Rutas públicas** (`/api/auth/login`, `/api/auth/register`): agrega solo `X-Internal-Request` sin claims de usuario
- **Rutas protegidas**: extrae y valida JWT del header `Authorization: Bearer <token>`, luego agrega `X-Internal-Request`, `X-User-Email`, `X-User-Id`, `X-User-Role` y establece el `SecurityContext` reactivo con `ReactiveSecurityContextHolder.withAuthentication()`
- Claims esperados en el JWT: `sub` (email), `userId` (Long), `role` (String)
- Usa `SimpleGrantedAuthority("ROLE_" + role)` para la autenticación

### JwtUtil.java
- Decodifica la clave HMAC con `Decoders.BASE64.decode(secret)` + `Keys.hmacShaKeyFor()`
- Métodos: `validateToken()`, `getClaims()`, `getUserId()`, `getRole()`, `isExpired()`

---

## 2. Eureka Server (`eureka-server/eureka`)

**Puerto:** 8761  
**Base package:** `com.gestion.eurekaserver.eureka`

### application.yaml
```yaml
server:
  port: 8761
spring:
  application:
    name: eureka
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: false
```

---

## 3. Auth Service (`auth-service/auth-service`)

**Puerto:** 8081 (variable `${SERVER_PORT:8081}`)  
**Base package:** `com.gestion.auth.auth_service`  
**Esquema BD:** `auth_service`  
**JPA DDL:** `${JPA_DDL_AUTO:create-drop}` (por defecto crea/borra)

### Dependencias clave
- `spring-boot-starter-webmvc`, `spring-boot-starter-security`
- `spring-boot-starter-data-jpa`, `postgresql`
- `spring-cloud-starter-netflix-eureka-client`, `spring-cloud-starter-openfeign`
- `jjwt-api/impl/jackson 0.12.6`, `lombok`

### application.yaml (adicional)
```yaml
jwt:
  secret: ${JWT_SECRET:Zd2W3jBD8D2xFUSNPywUHDFeuFunANkdKKnaIQzFjbj}
  expiration: ${JWT_EXPIRATION:3600000}
internal:
  gateway:
    secret: ${INTERNAL_GATEWAY_SECRET:5rO2rtldA8350502DreTcvFu9n2s8v7Xo1j6z9w0}
```

### Entidades
| Entidad | Tabla | Descripción |
|---|---|---|
| `Credencial` | `auth_service.credenciales` | user_id, correo, password (BCrypt), rol_id (FK → roles) |
| `Rol` | `auth_service.roles` | nombre: ADMIN, PACIENTE, MEDICO |

### SecurityConfig.java
- `@EnableWebSecurity`, `@RequiredArgsConstructor`
- Inyecta `InternalRequestFilter` y `JwtAuthenticationFilter` (ambos via constructor)
- Orden: `InternalRequestFilter` → `JwtAuthenticationFilter` → `UsernamePasswordAuthenticationFilter`
- Permite `/api/auth/login` y `/api/auth/credentials` sin autenticación
- Demás rutas requieren autenticación

### InternalRequestFilter.java (auth)
- Versión especial que **permite continuar** cuando `X-User-Id` o `X-User-Role` son null (rutas públicas)
- Si el header `X-Internal-Request` no coincide → 403
- Si hay claims → establece `SecurityContext` con `JwtUserDetails`

### JwtService.java
- Genera JWT con claims: `sub` (correo), `userId` (Long), `role` (String)
- Firma HMAC: `Decoders.BASE64.decode(jwtSecret)` + `Keys.hmacShaKeyFor()`
- Expiración configurable (`jwt.expiration`)

### JwtAuthenticationFilter.java
- Filtro tradicional `OncePerRequestFilter`
- Extrae token de `Authorization: Bearer`
- Valida JWT con `JwtService`
- Carga `CustomUserDetails` desde BD y establece `SecurityContext`

### Endpoints REST
| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| POST | `/api/auth/login` | Público | Login, devuelve JWT |
| POST | `/api/auth/credentials` | Público | Crear credencial (user_id, correo, password, rol) |

---

## 4. User Service (`user-service/user-service`)

**Puerto:** 8082  
**Base package:** `com.gestion.user.user_service`  
**Esquema BD:** `user_service`  
**JPA DDL:** `update`  
**Flyway:** habilitado

### Dependencias clave
- `spring-boot-starter-webmvc`, `spring-boot-starter-security`
- `spring-boot-starter-data-jpa`, `flyway`, `postgresql`
- `spring-cloud-starter-netflix-eureka-client`
- `lombok`, `validation`

### application.yaml (adicional)
```yaml
internal:
  gateway:
    secret: ${INTERNAL_GATEWAY_SECRET:5rO2rtldA8350502DreTcvFu9n2s8v7Xo1j6z9w0}
```

### Entidad
| Entidad | Tabla | Descripción |
|---|---|---|
| `Usuario` | `user_service.usuarios` | nombres, apellidos, telefono, direccion, fecha_nacimiento, tipo_usuario (ADMIN/PACIENTE/MEDICO), activo |

### SecurityConfig.java
- `@EnableWebSecurity`, `@EnableMethodSecurity`
- Inyecta `InternalRequestFilter` y `JwtAuthenticationEntryPoint`
- Orden: `InternalRequestFilter` → `UsernamePasswordAuthenticationFilter`
- Todas las rutas requieren autenticación (`.anyRequest().authenticated()`)
- `@EnableMethodSecurity` permite `@PreAuthorize` en los controllers

### InternalRequestFilter.java (user)
- Versión estándar: requiere `X-User-Id`, `X-User-Email`, `X-User-Role`
- Si faltan claims → 401
- Establece `SecurityContext` con `JwtUserDetails`

### JwtUserDetails.java
- Implementa `UserDetails`
- Authorities: `ROLE_` + role
- userId, correo, role inmutables

### JwtAuthenticationEntryPoint.java
- Retorna 401 con body JSON personalizado

### Endpoints REST
| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| GET | `/api/users` | `@PreAuthorize("hasRole('ADMIN')")` | Listar todos |
| POST | `/api/users` | `@PreAuthorize("hasRole('ADMIN')")` | Crear usuario |
| GET | `/api/users/{id}` | `@PreAuthorize("hasRole('ADMIN')")` | Obtener por ID |
| PUT | `/api/users/{id}` | `@PreAuthorize("hasRole('ADMIN')")` | Actualizar |
| DELETE | `/api/users/{id}` | `@PreAuthorize("hasRole('ADMIN')")` | Eliminar |

---

## 5. Medical Service (`medical-service/medical-service`)

**Puerto:** 8084  
**Base package:** `com.gestion.medical.medical_service`  
**Esquema BD:** `doctor_service`  
**JPA DDL:** `validate`  
**Flyway:** deshabilitado

### Dependencias clave
- `spring-boot-starter-webmvc`, `spring-boot-starter-security`
- `spring-boot-starter-data-jpa`, `flyway`, `postgresql`
- `spring-cloud-starter-netflix-eureka-client`
- `lombok`

### application.yaml (adicional)
```yaml
internal:
  gateway:
    secret: ${INTERNAL_GATEWAY_SECRET:5rO2rtldA8350502DreTcvFu9n2s8v7Xo1j6z9w0}
```

### Entidades
| Entidad | Tabla | Descripción |
|---|---|---|
| `Doctor` | `doctor_service.doctores` | usuario_id, especialidad_id (FK), cmp (colegiatura), disponible |
| `Especialidad` | `doctor_service.especialidades` | nombre, descripcion, activa |
| `HorarioDoctor` | `doctor_service.horarios_doctor` | doctor_id, dia_semana (LUNES–DOMINGO), hora_inicio, hora_fin, activo |

### SecurityConfig.java
- `@EnableWebSecurity`
- Inyecta `InternalRequestFilter`
- Orden: `InternalRequestFilter` → `UsernamePasswordAuthenticationFilter`
- Todas las rutas requieren autenticación

### InternalRequestFilter.java (medical)
- Versión estándar: requiere `X-User-Id`, `X-User-Email`, `X-User-Role`
- Si faltan claims → 401
- Establece `SecurityContext` con `JwtUserDetails`

### Endpoints REST
| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| GET | `/api/medicals/doctores` | Autenticado | Listar doctores |
| POST | `/api/medicals/doctores` | Autenticado | Crear doctor |
| GET | `/api/medicals/doctores/{id}` | Autenticado | Obtener doctor |
| PUT | `/api/medicals/doctores/{id}` | Autenticado | Actualizar doctor |
| DELETE | `/api/medicals/doctores/{id}` | Autenticado | Eliminar doctor |
| GET | `/api/medicals/especialidades` | Autenticado | Listar especialidades |
| POST | `/api/medicals/especialidades` | Autenticado | Crear especialidad |
| GET | `/api/medicals/especialidades/{id}` | Autenticado | Obtener especialidad |
| PUT | `/api/medicals/especialidades/{id}` | Autenticado | Actualizar especialidad |
| DELETE | `/api/medicals/especialidades/{id}` | Autenticado | Eliminar especialidad |

---

## 6. Cita Service (`cita-service/cita-service`)

**Puerto:** 8083  
**Base package:** `com.gestion.cita.cita_service`  
**Esquema BD:** `appointment_service`  
**JPA DDL:** `validate`  
**Flyway:** deshabilitado (con `baseline-on-migrate: true`)

### Dependencias clave
- `spring-boot-starter-webmvc`, `spring-boot-starter-security`
- `spring-boot-starter-data-jpa`, `flyway`, `postgresql`
- `spring-cloud-starter-netflix-eureka-client`
- `jjwt-api/impl/jackson 0.12.6`, `lombok`

### application.yaml (adicional)
```yaml
jwt:
  secret: ${JWT_SECRET:Zd2W3jBD8D2xFUSNPywUHDFeuFunANkdKKnaIQzFjbj}
  expiration: ${JWT_EXPIRATION:3600000}
internal:
  gateway:
    secret: ${INTERNAL_GATEWAY_SECRET:5rO2rtldA8350502DreTcvFu9n2s8v7Xo1j6z9w0}
```

### Entidad
| Entidad | Tabla | Descripción |
|---|---|---|
| `Cita` | `appointment_service.citas` | paciente_id, doctor_id, fecha, hora, motivo, estado (PENDIENTE/CONFIRMADA/CANCELADA/ATENDIDA) |

### SecurityConfig.java
- `@EnableWebSecurity`, `@EnableMethodSecurity`
- Inyecta `InternalRequestFilter`
- Orden: `InternalRequestFilter` → `UsernamePasswordAuthenticationFilter`
- Todas las rutas requieren autenticación

### InternalRequestFilter.java (cita)
- Versión estándar: requiere `X-User-Id`, `X-User-Email`, `X-User-Role`
- Si faltan claims → 401
- Establece `SecurityContext` con `JwtUserDetails`

### Endpoints REST
| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| GET | `/api/citas` | Autenticado | Listar citas con filtros |
| POST | `/api/citas` | Autenticado | Crear cita |
| GET | `/api/citas/{id}` | Autenticado | Obtener cita |
| PUT | `/api/citas/{id}` | Autenticado | Actualizar cita |
| DELETE | `/api/citas/{id}` | Autenticado | Cancelar cita |

---

## 7. Notification Service (`notification-service/notification-service`)

**Puerto:** 8085 (variable `${SERVER_PORT:8085}`)  
**Base package:** `com.gestion.notification.notification_service`  
**Esquema BD:** `notification_service` (predefinido en SQL, pero el servicio no tiene entities JPA)  
**Sin base de datos** en JPA

### Dependencias clave
- `spring-boot-starter-webmvc`, `spring-boot-starter-security`
- `spring-boot-starter-websocket`
- `spring-cloud-starter-netflix-eureka-client`
- `lombok`

### application.yaml
```yaml
server:
  port: ${SERVER_PORT:8085}
spring:
  application:
    name: notification-service
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
internal:
  gateway:
    secret: ${INTERNAL_GATEWAY_SECRET:5rO2rtldA8350502DreTcvFu9n2s8v7Xo1j6z9w0}
```

### SecurityConfig.java
- `@EnableWebSecurity`
- Inyecta `InternalRequestFilter`
- Orden: `InternalRequestFilter` → `UsernamePasswordAuthenticationFilter`
- Todas las rutas requieren autenticación

### InternalRequestFilter.java (notification)
- Versión estándar: requiere `X-User-Id`, `X-User-Email`, `X-User-Role`
- Si faltan claims → 401
- Establece `SecurityContext` con `JwtUserDetails`

---

## Flujo de Seguridad (Interno)

```
  Usuario                     API Gateway                     Microservicio
    │                             │                               │
    │   POST /api/auth/login      │                               │
    │───────────────────────────> │  (ruta pública)               │
    │                             │  agrega X-Internal-Request    │
    │                             │  (sin claims)                 │
    │                             │ ───────────────────────────>  │
    │                             │                               │ auth-service
    │                             │                               │ login → JWT
    │   <─── JWT ────────────     │                               │
    │                             │                               │
    │   GET /api/users            │                               │
    │   Authorization: Bearer JWT │                               │
    │───────────────────────────> │  valida JWT                   │
    │                             │  extrae claims                │
    │                             │  agrega headers:              │
    │                             │    X-Internal-Request         │
    │                             │    X-User-Email, X-User-Id    │
    │                             │    X-User-Role                │
    │                             │  establece SecurityContext    │
    │                             │ ───────────────────────────>  │ user-service
    │                             │                               │ InternalRequestFilter
    │                             │                               │ verifica X-Internal-Request
    │                             │                               │ establece SecurityContext
    │                             │                               │ @PreAuthorize("hasRole('ADMIN')")
    │                             │                               │ → 200 (o 403)
```

### Claves compartidas
| Variable | Propósito |
|---|---|
| `JWT_SECRET` | Firma/validación de JWT (Gateway + auth-service) |
| `INTERNAL_GATEWAY_SECRET` | Header `X-Internal-Request` para comunicación interna |
| Ambas se definen en `docker-compose.yml` con valores explícitos |

### InternalRequestFilter (comportamiento)
1. Verifica que `X-Internal-Request` coincida con `internal.gateway.secret`
   - No coincide → **403** "Acceso directo no permitido"
2. Lee `X-User-Id`, `X-User-Email`, `X-User-Role`
   - **auth-service**: si son null (ruta pública), continúa sin autenticar
   - **demás servicios**: si son null → **401** "Claims de usuario ausentes"
3. Crea `JwtUserDetails` y establece `UsernamePasswordAuthenticationToken` en el `SecurityContext`
4. Continúa la cadena de filtros

---

## Base de Datos

**PostgreSQL 16** en puerto `5433` (host) / `5432` (red interna Docker).

### Esquemas
| Esquema | Servicio | Propósito |
|---|---|---|
| `auth_service` | auth-service | roles, credenciales |
| `user_service` | user-service | usuarios (perfiles generales) |
| `patient_service` | — (predefinido) | información de pacientes |
| `doctor_service` | medical-service | doctores, especialidades, horarios |
| `appointment_service` | cita-service | citas médicas |
| `report_service` | — (predefinido) | reportes médicos |
| `notification_service` | notification-service | notificaciones |

### Script inicial
`postgresql/init_microservices.sql` crea todos los esquemas, tablas, índices y datos iniciales (roles: ADMIN, PACIENTE, MEDICO; especialidades: Cardiología, Pediatría, Dermatología).

---

## Docker Compose

`docker-compose.yml` levanta todos los servicios:

| Servicio | Puerto Host | Puerto Contenedor |
|---|---|---|
| postgres | 5433 | 5432 |
| eureka-server | 8761 | 8761 |
| api-gateway | 8080 | 8080 |
| auth-service | — | 8081 |
| user-service | — | 8082 |
| cita-service | — | 8083 |
| medical-service | — | 8084 |
| notification-service | — | 8085 |

Todos los microservicios se comunican por la red interna `clinica-net`. Solo `postgres`, `eureka-server` y `api-gateway` exponen puertos al host.
