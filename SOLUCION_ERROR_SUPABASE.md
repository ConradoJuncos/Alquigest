# 🔧 Solución al Error de Conexión con Supabase en Render

## 📌 Problema Identificado

El error `Network unreachable` y `The connection attempt failed` indica que tu aplicación en Render no puede conectarse a Supabase. 

**Causa principal**: Falta la configuración SSL requerida por Supabase y las variables de entorno pueden no estar correctamente configuradas.

## ✅ Cambios Realizados

He realizado los siguientes cambios en tu proyecto:

### 1. Creado `application-prod.properties`
**Ubicación**: `backend/src/main/resources/application-prod.properties`

Este archivo incluye:
- ✅ Configuración SSL para Supabase (`sslmode=require`)
- ✅ Variables de entorno parametrizadas
- ✅ Configuración optimizada de HikariCP para conexiones estables
- ✅ Logging adecuado para producción

### 2. Actualizado `Dockerfile`
**Cambios**:
- ✅ Activado perfil de producción (`SPRING_PROFILES_ACTIVE=prod`)
- ✅ Configurado para usar la variable `PORT` de Render
- ✅ Puerto por defecto 8080

### 3. Documentación
- ✅ `RENDER_ENV_CONFIG.md`: Guía detallada para configurar variables de entorno

---

## 🚀 Pasos Siguientes (DEBES HACERLOS TÚ)

### PASO 1: Obtener Credenciales de Supabase

1. Ve a tu proyecto en [Supabase](https://supabase.com)
2. Navega a: **Settings** → **Database**
3. Busca la sección **Connection Pooling** (RECOMENDADO)
4. Anota estos datos:

```
Host: aws-0-[region].pooler.supabase.com
Port: 6543 (connection pooling) o 5432 (directo)
Database: postgres
User: postgres.[tu-proyecto-ref]
Password: [la que configuraste al crear el proyecto]
```

**💡 TIP**: Si ves una URI completa como:
```
postgresql://postgres.xxxx:password@aws-0-us-east-1.pooler.supabase.com:6543/postgres
```

Puedes extraer todos los valores de ahí.

### PASO 2: Configurar Variables de Entorno en Render

1. Ve a tu servicio en [Render](https://render.com)
2. Ve a **Environment** en el menú lateral
3. Agrega estas variables (clic en "Add Environment Variable"):

```
DATABASE_HOST = aws-0-[region].pooler.supabase.com
DATABASE_PORT = 6543
DATABASE_NAME = postgres
DATABASE_USERNAME = postgres.[tu-proyecto-ref]
DATABASE_PASSWORD = [tu-password]
JWT_SECRET = [genera-una-clave-larga-y-segura]
```

**⚠️ IMPORTANTE**:
- NO uses comillas en los valores
- NO dejes espacios al inicio o final
- Usa **Connection Pooling** (puerto 6543) en lugar de conexión directa
- El `DATABASE_USERNAME` puede incluir un sufijo como `postgres.abcdefghijklmnop`

**Ejemplo de JWT_SECRET**:
```
JWT_SECRET = mySecretKeyForJWTTokenGenerationThatIsLongEnoughToMeetThe256BitRequirementForHMACSHA256Algorithm2024Production
```

### PASO 3: Commit y Push de los Cambios

Ejecuta estos comandos en tu terminal:

```bash
git add .
git commit -m "Fix: Configuración SSL para Supabase y perfil de producción"
git push origin main
```

### PASO 4: Re-Deploy en Render

Render debería detectar automáticamente los cambios y hacer un nuevo deploy.

Si no lo hace automáticamente:
1. Ve a tu servicio en Render
2. Clic en **Manual Deploy** → **Deploy latest commit**

### PASO 5: Verificar los Logs

1. Mientras se despliega, ve a la pestaña **Logs** en Render
2. Busca estos mensajes de éxito:
   - `HikariPool-1 - Start completed.`
   - `Started AlquigestApplication in X seconds`

3. Si ves errores, verifica:
   - ✅ Las variables de entorno están bien escritas
   - ✅ No hay espacios extra en los valores
   - ✅ La contraseña es correcta
   - ✅ Estás usando el host correcto de Supabase

---

## 🔍 Verificación de Configuración Correcta

### En Supabase:
- [ ] El proyecto está creado y activo
- [ ] Has copiado las credenciales de **Connection Pooling** (preferido)
- [ ] Has anotado el password (no se puede recuperar después)

### En Render:
- [ ] Todas las 6 variables de entorno están configuradas
- [ ] No hay espacios ni comillas extra
- [ ] El Root Directory está configurado como `backend`
- [ ] El Environment está configurado como `Docker`

### En tu Repositorio:
- [ ] Has hecho commit de los cambios
- [ ] Has hecho push a GitHub
- [ ] Los archivos nuevos están en el repositorio

---

## 🐛 Troubleshooting

### Error: "SSL connection required"
**Solución**: Ya está solucionado en `application-prod.properties` con `sslmode=require`

### Error: "Authentication failed"
**Solución**: Verifica `DATABASE_PASSWORD` y `DATABASE_USERNAME` en Render

### Error: "Network unreachable"
**Solución**: 
1. Verifica que `DATABASE_HOST` sea el correcto de Supabase
2. Usa Connection Pooling (puerto 6543) en lugar de conexión directa
3. Asegúrate de que Render tenga acceso a internet (debería tenerlo siempre)

### Error: "Database does not exist"
**Solución**: Verifica que `DATABASE_NAME` sea exactamente `postgres` (minúsculas)

---

## 📚 Archivos de Referencia

- `RENDER_ENV_CONFIG.md`: Guía detallada de configuración de variables de entorno
- `backend/Dockerfile`: Configuración de Docker para deployment
- `backend/src/main/resources/application-prod.properties`: Configuración de producción

---

## ✨ Resultado Esperado

Una vez completados todos los pasos, deberías ver:

1. ✅ Build exitoso en Render
2. ✅ Aplicación corriendo sin errores
3. ✅ Conexión exitosa a Supabase
4. ✅ Tablas creadas automáticamente en Supabase (por `ddl-auto=update`)

Tu API estará disponible en: `https://[tu-servicio].onrender.com`


