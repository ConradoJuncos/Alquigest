# Alquigest - Sistema de Gestión de Alquileres

Sistema completo para la gestión de alquileres de inmuebles, dividido en backend (API REST) y frontend (interfaz de usuario).

## Estructura del Proyecto

```
Alquigest/
├── backend/                    # API REST con Spring Boot
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── frontend/                   # Interfaz de usuario (por desarrollar)
│   └── README.md
└── README.md                   # Este archivo
```

## Tecnologías

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **SQLite** (Base de datos)
- **Maven** (Gestión de dependencias)
- **Swagger/OpenAPI** (Documentación de API)

### Frontend
- Por definir (React, Vue.js, Angular, etc.)

## Inicio Rápido

### Backend
```bash
cd backend
mvn spring-boot:run
```
La API estará disponible en: `http://localhost:8081`

### Frontend
La carpeta frontend está preparada para el desarrollo futuro de la interfaz de usuario.

## Documentación

- **Backend**: Ver `backend/README.md`
- **Frontend**: Ver `frontend/README.md`
- **API Docs**: `http://localhost:8081/swagger-ui.html` (después de ejecutar el backend)

## Funcionalidades Implementadas

### ✅ Backend (API REST)
- **Inmuebles**: CRUD completo
- **Propietarios**: CRUD completo
- **Base de datos SQLite** con esquemas optimizados
- **Documentación automática** con Swagger
- **Configuración CORS** para integración frontend

### 🔄 Frontend (En desarrollo)
- Interfaz de usuario por implementar
- Integración con API del backend
- Gestión de inmuebles y propietarios

## APIs Disponibles

### Inmuebles
- `GET /api/inmuebles` - Listar todos
- `GET /api/inmuebles/{id}` - Obtener por ID
- `POST /api/inmuebles` - Crear nuevo
- `PUT /api/inmuebles/{id}` - Actualizar
- `DELETE /api/inmuebles/{id}` - Eliminar

### Propietarios
- `GET /api/propietarios` - Listar todos  
- `GET /api/propietarios/{id}` - Obtener por ID
- `POST /api/propietarios` - Crear nuevo
- `PUT /api/propietarios/{id}` - Actualizar
- `DELETE /api/propietarios/{id}` - Eliminar

## Requisitos

- **Java 17** o superior
- **Maven 3.6** o superior
- **Git**
