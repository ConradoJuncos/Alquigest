# Guía de Despliegue - Alquigest en Render + Supabase

## ✅ Cambios Realizados para Compatibilidad con PostgreSQL

### 1. **Dependencias Actualizadas** ✅
- Agregada dependencia de PostgreSQL en `pom.xml`
- Mantenida dependencia de SQLite para desarrollo local

### 2. **Entidad Inquilino Corregida** ✅
- Eliminado `columnDefinition` específico de SQLite
- Campo `estaAlquilando` ahora es compatible con PostgreSQL

### 3. **Configuración de Perfiles** ✅
- **Desarrollo (SQLite)**: `application.properties` (sin cambios)
- **Producción (PostgreSQL)**: `application-prod.properties` (nuevo)

### 4. **Compatibilidad Verificada** ✅
- ✅ Tipos de datos compatibles (String, Boolean, BigDecimal, Integer, Long)
- ✅ Relaciones JPA estándar (ManyToOne, OneToMany, ManyToMany)
- ✅ GenerationType.AUTO (funciona correctamente con PostgreSQL)
- ✅ Sin uso de dialectos específicos de SQLite en consultas

---

## 📋 Paso a Paso para Despliegue

### PARTE 1: Configurar Base de Datos en Supabase

#### 1. Crear Proyecto en Supabase
1. Ir a [supabase.com](https://supabase.com) e iniciar sesión
2. Click en "New Project"
3. Configurar:
   - **Name**: alquigest
   - **Database Password**: (crear una contraseña segura y guardarla)
   - **Region**: South America (más cercano)
4. Click en "Create new project" y esperar que se cree

#### 2. Obtener Credenciales de Conexión
1. En el panel de Supabase, ir a **Settings** → **Database**
2. En la sección "Connection string", copiar la URL de conexión:
   - Seleccionar el modo "URI" o "Connection pooling"
   - Copiar la URI completa (ejemplo: `postgresql://postgres.xxxxx:password@aws-0-us-east-1.pooler.supabase.com:5432/postgres`)
3. Guardar esta información:
   ```
   DATABASE_URL=postgresql://postgres.[project-ref]:[password]@[host]:5432/postgres
   DATABASE_USERNAME=postgres
   DATABASE_PASSWORD=[tu-password]
   ```

---

### PARTE 2: Configurar Backend en Render

#### 1. Preparar el Repositorio
1. Asegurarse de que todos los cambios estén commiteados en Git:
   ```bash
   git add .
   git commit -m "Preparado para despliegue con PostgreSQL"
   git push origin main
   ```

#### 2. Crear Web Service en Render
1. Ir a [render.com](https://render.com) e iniciar sesión
2. Click en "New +" → "Web Service"
3. Conectar repositorio de GitHub:
   - Autorizar acceso a GitHub
   - Seleccionar el repositorio "Alquigest"

#### 3. Configurar el Web Service
Completar el formulario con:

- **Name**: `alquigest-backend`
- **Region**: Oregon (US West) o la más cercana
- **Branch**: `main`
- **Root Directory**: `backend`
- **Environment**: `Docker` (seleccionar esta opción)
- **Instance Type**: Free

**Importante**: En lugar de usar las opciones predefinidas de Language y Runtime, vamos a usar un Dockerfile personalizado.

#### 3.1. Crear Dockerfile en el directorio backend

Crear un archivo llamado `Dockerfile` en la raíz del directorio `backend`:

```dockerfile
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]

#### 4. Configurar Variables de Entorno
En la sección "Environment Variables", agregar:

| Key | Value |
|-----|-------|
| `DATABASE_URL` | (URL de Supabase copiada) |
| `DATABASE_USERNAME` | `postgres` |
| `DATABASE_PASSWORD` | (password de Supabase) |
| `JWT_SECRET` | `mySecretKeyForJWTTokenGenerationThatIsLongEnoughToMeetThe256BitRequirementForHMACSHA256Algorithm` |
| `SPRING_PROFILES_ACTIVE` | `prod` |

#### 5. Desplegar
1. Click en "Create Web Service"
2. Render comenzará a construir y desplegar automáticamente
3. Ver los logs en tiempo real para verificar que no haya errores

---

### PARTE 3: Verificación Post-Despliegue

#### 1. Verificar que la Aplicación Esté Corriendo
1. Una vez completado el despliegue, Render proporcionará una URL (ejemplo: `https://alquigest-backend.onrender.com`)
2. Acceder a: `https://alquigest-backend.onrender.com/swagger-ui/index.html`
3. Verificar que Swagger UI se cargue correctamente

#### 2. Verificar Conexión a Base de Datos
1. Revisar los logs en Render
2. Buscar mensajes como:
   - `HikariPool-1 - Starting...`
   - `HikariPool-1 - Start completed`
   - Inicialización de tablas por Hibernate

#### 3. Crear Usuario Inicial
1. En Swagger UI, usar el endpoint `/api/auth/signup` para crear el primer usuario administrador
2. Hacer login con `/api/auth/login` y verificar que funcione

---

## 🔧 Solución de Problemas Comunes

### Error: "Cannot connect to database"
- Verificar que las variables de entorno estén correctamente configuradas
- Verificar que la IP de Render esté permitida en Supabase (por defecto debería estar permitida)
- Verificar que la contraseña no tenga caracteres especiales que necesiten encoding

### Error: "Port already in use"
- Asegurarse de que el comando de inicio use `$PORT` (variable de entorno de Render)

### Error: "Application failed to start"
- Revisar los logs completos en Render
- Verificar que el perfil `prod` esté activado correctamente

---

## 📊 Datos de Prueba Iniciales

### Usuario Administrador por Defecto
Después del despliegue, crear manualmente a través de Swagger:
```json
{
  "username": "admin",
  "email": "admin@alquigest.com",
  "password": "Admin123!",
  "roles": ["ROLE_ADMINISTRADOR"]
}
```

---

## 🔄 Actualización de Cambios

Para desplegar nuevos cambios:
1. Hacer commit y push al repositorio:
   ```bash
   git add .
   git commit -m "Descripción del cambio"
   git push origin main
   ```
2. Render detectará automáticamente los cambios y redesplegará

---

## 📝 Notas Importantes

1. **Plan Gratuito de Render**:
   - La aplicación se dormirá después de 15 minutos de inactividad
   - El primer request después de dormir tomará ~30 segundos

2. **Plan Gratuito de Supabase**:
   - Base de datos pausada después de 7 días de inactividad
   - 500 MB de almacenamiento
   - 2 GB de transferencia mensual

3. **Seguridad**:
   - Cambiar `JWT_SECRET` a un valor único y seguro en producción
   - No compartir las credenciales de la base de datos
   - Considerar usar HTTPS siempre (Render lo proporciona automáticamente)

4. **Backup**:
   - Supabase permite descargar backups de la base de datos
   - Configurar backups regulares según necesidad

---

## ✅ Checklist de Despliegue

- [ ] Cuenta creada en Supabase
- [ ] Proyecto de base de datos creado en Supabase
- [ ] Credenciales de base de datos guardadas
- [ ] Código commiteado y pusheado a GitHub
- [ ] Cuenta creada en Render
- [ ] Web Service creado en Render
- [ ] Variables de entorno configuradas
- [ ] Aplicación desplegada exitosamente
- [ ] Swagger UI accesible
- [ ] Usuario administrador creado
- [ ] Endpoints probados

---

## 🎉 ¡Listo para Producción!

Una vez completados todos los pasos, tu sistema estará desplegado y accesible desde cualquier lugar.

