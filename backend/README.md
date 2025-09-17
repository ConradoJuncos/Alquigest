# Alquigest Backend

API REST para el sistema de gestión de alquileres desarrollado con Spring Boot.

## Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **SQLite** (Base de datos)
- **Swagger/OpenAPI** (Documentación)
- **Maven** (Gestión de dependencias)

## Estructura del proyecto

```
backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/alquileres/
│       │       ├── controller/     # Controladores REST
│       │       ├── service/        # Lógica de negocio
│       │       ├── repository/     # Acceso a datos
│       │       ├── model/          # Entidades JPA
│       │       ├── dto/            # Data Transfer Objects
│       │       └── config/         # Configuraciones
│       └── resources/
│           ├── application.properties
│           └── database/
│               └── alquileres.db
├── pom.xml
└── target/
```

## Configuración

La aplicación utiliza SQLite como base de datos y se ejecuta en el puerto **8081**.

### Variables de configuración principales:
- `server.port=8081`
- `spring.datasource.url=jdbc:sqlite:src/main/resources/database/alquileres.db`

## Ejecución

### Prerequisitos
- Java 17 o superior
- Maven 3.6 o superior

### Comandos para ejecutar

```bash
# Desde la carpeta backend
cd backend

# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn spring-boot:run

# O ejecutar el JAR generado
mvn clean package
java -jar target/alquigest-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en: `http://localhost:8081`

## Documentación API

Una vez ejecutada la aplicación, puedes acceder a:

- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **API Docs (JSON)**: `http://localhost:8081/api-docs`

## Endpoints principales

### Inmuebles
- `GET /api/inmuebles` - Obtener todos los inmuebles
- `GET /api/inmuebles/{id}` - Obtener inmueble por ID
- `POST /api/inmuebles` - Crear nuevo inmueble
- `PUT /api/inmuebles/{id}` - Actualizar inmueble
- `DELETE /api/inmuebles/{id}` - Eliminar inmueble

### Propietarios
- `GET /api/propietarios` - Obtener todos los propietarios
- `GET /api/propietarios/{id}` - Obtener propietario por ID
- `POST /api/propietarios` - Crear nuevo propietario
- `PUT /api/propietarios/{id}` - Actualizar propietario
- `DELETE /api/propietarios/{id}` - Eliminar propietario

## Base de datos

El sistema utiliza SQLite con las siguientes entidades principales:

### Inmuebles
- ID (autoincremental)
- Propietario ID (FK)
- Dirección
- Tipo de inmueble ID
- Tipo
- Estado
- Superficie
- Es alquilado
- Es activo
- Fechas de creación y actualización

### Propietarios
- ID (autoincremental)
- Nombre
- Apellido
- DNI (único)
- Teléfono
- Email (único)
- Dirección
- Es activo
- Fechas de creación y actualización
