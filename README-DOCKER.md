# Alquigest - Sistema de Gestión de Alquileres

## 🚀 Ejecución con Docker

Este proyecto está configurado para ejecutarse completamente con Docker, tanto en desarrollo como en producción.

### Prerrequisitos

- Docker Desktop instalado
- Docker Compose instalado (viene incluido con Docker Desktop)

### Comandos

#### Iniciar la aplicación
```bash
docker-compose up --build
```

#### Iniciar en segundo plano (detached mode)
```bash
docker-compose up -d --build
```

#### Ver logs
```bash
# Ver todos los logs
docker-compose logs -f

# Ver solo logs del backend
docker-compose logs -f backend

# Ver solo logs de PostgreSQL
docker-compose logs -f postgres
```

#### Detener la aplicación
```bash
docker-compose down
```

#### Detener y eliminar datos (reset completo)
```bash
docker-compose down -v
```

#### Reconstruir solo el backend
```bash
docker-compose up -d --build backend
```

### Acceso a la aplicación

- **Backend API**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui/index.html
- **PostgreSQL**: localhost:5432
  - Database: `alquileres`
  - Username: `postgres`
  - Password: `postgres123`

### Usuario Administrador por Defecto

Al iniciar por primera vez, se creará automáticamente un usuario administrador:

- **Username**: `admin`
- **Password**: `123456`

### Estructura del Proyecto

```
Alquigest/
├── backend/
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   └── .dockerignore
├── frontend/
├── docker-compose.yml
└── README.md
```

### Configuración de Variables de Entorno

Las variables de entorno están configuradas en `docker-compose.yml`. Para modificarlas:

```yaml
environment:
  DATABASE_HOST: postgres
  DATABASE_PORT: 5432
  DATABASE_NAME: alquileres
  DATABASE_USERNAME: postgres
  DATABASE_PASSWORD: postgres123
  SERVER_PORT: 8081
  JWT_SECRET: your-secret-here
```

### Despliegue en Render

Para desplegar en Render, configura estas variables de entorno en el dashboard:

**Variables de Entorno Requeridas:**

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DATABASE_HOST` | Host de PostgreSQL | `db.xxx.supabase.co` o host de Render |
| `DATABASE_PORT` | Puerto de PostgreSQL | `5432` |
| `DATABASE_NAME` | Nombre de la base de datos | `postgres` o `alquileres` |
| `DATABASE_USERNAME` | Usuario de PostgreSQL | `postgres` |
| `DATABASE_PASSWORD` | Contraseña de PostgreSQL | `tu-password-segura` |
| `JWT_SECRET` | Secreto para JWT (debe ser largo y seguro) | `mySecretKeyForJWT...` |

**NO configures**: `SPRING_PROFILES_ACTIVE` - ya no es necesario.

**Configuración del Servicio en Render:**

1. **Tipo**: Web Service
2. **Lenguaje**: Docker (usa el Dockerfile incluido)
3. **Root Directory**: `backend`
4. **Build Command** (si no usas Docker):
   ```bash
   mvn clean package -DskipTests
   ```
5. **Start Command** (si no usas Docker):
   ```bash
   java -Dserver.port=$PORT -jar target/*.jar
   ```

**Si usas Dockerfile (recomendado):**
- Render detectará automáticamente el `backend/Dockerfile`
- Solo necesitas configurar las variables de entorno

### Troubleshooting

#### El backend no se conecta a PostgreSQL
```bash
# Verificar que PostgreSQL esté corriendo
docker-compose ps

# Ver logs de PostgreSQL
docker-compose logs postgres

# Reiniciar servicios
docker-compose restart
```

#### Problemas de permisos o compilación
```bash
# Limpiar todo y empezar de nuevo
docker-compose down -v
docker system prune -f
docker-compose up --build
```

#### Conectarse manualmente a PostgreSQL
```bash
docker exec -it alquileres-postgres psql -U postgres -d alquileres
```

### Desarrollo

Este proyecto usa PostgreSQL tanto para desarrollo como para producción. No hay diferencia entre entornos de desarrollo y producción en cuanto a la base de datos.

Los cambios en el código se reflejarán reconstruyendo el contenedor:
```bash
docker-compose up -d --build backend
```
