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
│       │       ├── config/         # Configuraciones
│       │       ├── controller/     # Controladores REST
│       │       ├── dto/            # Data Transfer Objects
│       │       ├── exception/      # Manejo de excepciones
│       │       ├── model/          # Entidades JPA
│       │       ├── repository/     # Acceso a datos
│       │       ├── security/       # Seguridad
│       │       └── service/        # Lógica de negocio
│       └── resources/
│           ├── application.properties
│           └── database/
│               └── alquileres.db
├── pom.xml
├── Notas
└── target/
```

## Configuración

La aplicación utiliza SQLite como base de datos y se ejecuta en el puerto **8081**.

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

- **Swagger UI**: `http://localhost:8081/swagger-ui/index.html`