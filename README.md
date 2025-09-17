# Alquigest - Sistema de Gestión de Alquileres

Backend desarrollado en Java 17 con Spring Boot 3.x para la gestión de propiedades en alquiler.

## Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **SQLite** (Base de datos)
- **Maven** (Gestión de dependencias)
- **Swagger/OpenAPI** (Documentación de API)

## Requisitos Previos

- Java 17 o superior
- Maven 3.6 o superior
- Git

## Instalación y Ejecución

### 1. Clonar el repositorio
```bash
git clone <url-del-repositorio>
cd Alquigest
```

### 2. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

La aplicación se iniciará en `http://localhost:8080`

### 3. Verificar funcionamiento
- **API Base**: http://localhost:8080/api/propiedades
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/alquileres/
│   │   ├── AlquigestApplication.java       # Clase principal
│   │   ├── config/
│   │   │   └── CorsConfig.java             # Configuración CORS
│   │   ├── controller/
│   │   │   └── PropiedadController.java    # Controlador REST
│   │   ├── dto/
│   │   │   └── PropiedadDTO.java           # Data Transfer Object
│   │   ├── model/
│   │   │   └── Propiedad.java              # Entidad JPA
│   │   ├── repository/
│   │   │   └── PropiedadRepository.java    # Repositorio JPA
│   │   └── service/
│   │       └── PropiedadService.java       # Lógica de negocio
│   └── resources/
│       ├── application.properties          # Configuración
│       └── database/                       # Base de datos SQLite
└── test/                                   # Tests unitarios
```

## API Endpoints

### Propiedades
- `GET /api/propiedades` - Obtener todas las propiedades
- `GET /api/propiedades/disponibles` - Obtener propiedades disponibles
- `GET /api/propiedades/{id}` - Obtener propiedad por ID
- `POST /api/propiedades` - Crear nueva propiedad
- `PUT /api/propiedades/{id}` - Actualizar propiedad
- `DELETE /api/propiedades/{id}` - Eliminar propiedad
- `GET /api/propiedades/buscar?texto=...` - Buscar por texto
- `GET /api/propiedades/precio?precioMin=...&precioMax=...` - Buscar por rango de precio
- `PATCH /api/propiedades/{id}/disponibilidad?disponible=...` - Cambiar disponibilidad

### Ejemplo de Propiedad (JSON)
```json
{
  "titulo": "Apartamento en el centro",
  "descripcion": "Hermoso apartamento de 2 habitaciones en el centro de la ciudad",
  "direccion": "Calle Principal 123, Ciudad",
  "precio": 800.00,
  "disponible": true
}
```

## Base de Datos

La aplicación utiliza SQLite con las siguientes características:
- **Archivo**: `src/main/resources/database/alquileres.db`
- **Creación automática**: Se crea automáticamente al ejecutar la aplicación
- **DDL Auto**: `update` - Las tablas se crean/actualizan automáticamente

## CORS

Configurado para permitir requests desde:
- `http://localhost:3000` (Frontend React)

## Desarrollo y Testing

### Ejecutar tests
```bash
mvn test
```

### Compilar sin ejecutar
```bash
mvn compile
```

### Limpiar y compilar
```bash
mvn clean compile
```

## Configuración para Producción

Para usar en producción, modificar `application.properties`:
- Cambiar la URL de la base de datos
- Configurar logging apropiado
- Ajustar configuración de CORS según sea necesario

## Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear un Pull Request
