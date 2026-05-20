# Alquigest - Sistema de Gestión de Alquileres

## Stack tecnológico

| Componente | Tecnología |
|-----------|-----------|
| Backend | Java 21, Spring Boot 3.2.0 |
| Frontend | Next.js 14 (App Router), React 18, TypeScript, TailwindCSS |
| Base de datos | PostgreSQL (Supabase en producción) |
| Caché | Redis en producción, ConcurrentMap en desarrollo |
| Autenticación | JWT en cookies HttpOnly |
| Email | Resend API |
| Deploy | Backend en Render, frontend en Vercel |

---

## Usuarios y roles

El sistema tiene 3 usuarios fijos:

| Rol | Permisos |
|-----|---------|
| **ABOGADA** | Lectura y escritura total (equivale a admin) |
| **SECRETARIA** (×2) | Solo lectura |
| **ADMINISTRADOR** | Configuración del sistema (no usado activamente) |

---

## Estructura del proyecto

```
alquigest/
├── backend/                    # API REST Spring Boot
│   └── src/main/java/com/alquileres/
│       ├── config/             # SecurityConfig, CacheConfig, CacheNames
│       ├── controller/         # ~30 controladores REST
│       ├── dto/                # ~70 DTOs (Create, Update, Response)
│       ├── exception/          # GlobalExceptionHandler, BusinessException, ErrorCodes
│       ├── model/              # ~20 entidades JPA
│       ├── repository/         # ~20 repositorios Spring Data JPA
│       ├── scheduler/          # Tareas programadas
│       ├── security/           # JwtAuthenticationFilter, JwtUtils, EncryptionService
│       ├── service/            # ~30 servicios de negocio
│       └── util/               # FechaUtil, BCRAApiClient, ClockService
└── frontend/                   # Next.js App
    ├── app/                    # Rutas (App Router)
    ├── components/             # Componentes React reutilizables
    ├── contexts/               # AuthProvider
    ├── hooks/                  # Custom hooks
    ├── types/                  # Interfaces TypeScript
    └── utils/                  # fetchWithToken, servicios API, backendURL
```

---

## Entidades principales

| Entidad | Descripción |
|---------|-------------|
| `Contrato` | Contrato de alquiler entre inquilino e inmueble |
| `Inmueble` | Propiedad a alquilar |
| `Inquilino` | Persona que alquila |
| `Propietario` | Dueño del inmueble (clave fiscal encriptada con AES) |
| `Alquiler` | Registro mensual de alquiler |
| `AumentoAlquiler` | Historial de aumentos (ICL o manual) |
| `ServicioContrato` | Servicios asociados a un contrato (agua, gas, etc.) |
| `PagoServicio` | Pagos de servicios |
| `CancelacionContrato` | Rescisión de contrato |
| `Usuario` / `Rol` | Usuarios del sistema y sus roles |
| `EstadoContrato` | Vigente / No Vigente / Cancelado |
| `EstadoInmueble` | Disponible / Alquilado / Mantenimiento |

---

## Rutas del frontend

| Ruta | Descripción |
|------|-------------|
| `/contratos` | Listado de contratos |
| `/contratos/nuevo` | Crear contrato |
| `/contratos/[id]` | Detalle (servicios, PDF, historial) |
| `/inmuebles` | CRUD de inmuebles |
| `/inquilinos` | CRUD de inquilinos |
| `/propietarios` | CRUD de propietarios |
| `/alquileres` | Alquileres vigentes y pagos |
| `/alquileres/aumentos-manuales` | Aumentos fuera de ICL |
| `/pago-servicios` | Registro de pagos de servicios |
| `/informes` | Dashboard de reportes (alquileres, aumentos, honorarios) |
| `/equipo` | Gestión de usuarios |
| `/auth/login` | Login |
| `/auth/recuperar-contrasena` | Recuperación de contraseña por código |

---

## Endpoints principales del backend

Prefijo: `/api`

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/auth/signin` | Login |
| POST | `/auth/logout` | Logout |
| GET | `/auth/me` | Verificar sesión activa |
| GET/POST | `/contratos` | Listar / crear contratos |
| GET | `/contratos/vigentes` | Solo contratos vigentes |
| GET | `/contratos/proximos-vencer?diasAntes=N` | Próximos a vencer |
| GET/POST | `/alquileres` | Alquileres |
| GET | `/alquileres/honorarios` | Cálculo de honorarios |
| POST | `/alquileres/{id}/pagos` | Registrar pago |
| GET/POST | `/inmuebles` | CRUD inmuebles |
| GET/POST | `/inquilinos` | CRUD inquilinos |
| GET/POST | `/propietarios` | CRUD propietarios |
| GET/POST | `/servicios-contrato` | Servicios por contrato |
| GET/POST | `/pagos-servicios` | Pagos de servicios |
| GET | `/informes/**` | Reportes |

---

## Flujo de negocio: creación de contrato

1. Usuario (ABOGADA) llena formulario en `/contratos/nuevo`
2. Frontend envía `POST /api/contratos`
3. Backend valida: inmueble disponible, inquilino existente, no hay contrato vigente para ese inmueble
4. Se crea el contrato, se actualiza el estado del inmueble a "Alquilado"
5. Se genera el primer `Alquiler` mensual; si la fecha de inicio es pasada, se generan alquileres retroactivos
6. El frontend recibe un DTO enriquecido con datos del propietario (desencriptados), tipo de inmueble y último alquiler

---

## Aspectos técnicos relevantes

**Fechas**: las entidades almacenan fechas como `String` en formato ISO (`yyyy-MM-dd`). La conversión a formato de usuario (`dd/MM/yyyy`) se hace en `FechaUtil`.

**Encriptación**: la clave fiscal de propietarios se almacena encriptada con AES (`EncryptionService`).

**Caché**: los contratos y sus listados derivados están cacheados con Redis (TTL 1 hora). Los nombres están centralizados en `CacheNames.java`.

**API BCRA**: `BCRAApiClient` consume la API del Banco Central para obtener el índice ICL, con reintentos automáticos.

**Autenticación**: JWT en cookie HttpOnly con `SameSite=None; Secure`. El frontend usa `credentials: 'include'` en todos los requests. El token se valida en `JwtAuthenticationFilter` antes de cada request.

---

## Deuda técnica conocida

### N+1 queries en `enrichContratoDTO`
Cada contrato en una lista genera 3 queries adicionales (propietario, tipo de inmueble, último alquiler). El caché Redis mitiga el impacto en producción. Pendiente resolver con JOINs o projecciones JPQL cuando sea prioritario.


### Bugs conocidos
_(se irán registrando aquí a medida que se identifiquen y corrijan)_
