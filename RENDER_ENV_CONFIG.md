# Configuración de Variables de Entorno en Render

## ⚠️ IMPORTANTE: Configuración de Supabase

Supabase requiere conexiones SSL. Asegúrate de seguir estos pasos EXACTAMENTE.

## 📋 Variables de Entorno Requeridas en Render

En el dashboard de Render, en la sección **Environment Variables**, agrega las siguientes variables:

### 1. Variables de Base de Datos

#### Opción A: Usando la cadena de conexión completa de Supabase

Si Supabase te da una URL como:
```
postgresql://postgres.xxxxx:[YOUR-PASSWORD]@aws-0-us-east-1.pooler.supabase.com:6543/postgres
postgresql://postgres:QQahFFn0xK0B1L5H@db.ksutxbemxujpletozsss.supabase.co:5432/postgres
```

Debes separar la información así:

```
DATABASE_HOST=db.ksutxbemxujpletozsss.supabase.co
DATABASE_PORT=5432
DATABASE_NAME=postgres
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=QQahFFn0xK0B1L5H
```

#### Opción B: Desde el Dashboard de Supabase

1. Ve a tu proyecto en Supabase
2. Ve a **Settings** → **Database**
3. Busca la sección **Connection Info** o **Connection Pooling**
4. Copia los valores:

```
DATABASE_HOST=db.[tu-proyecto].supabase.co
   O BIEN
DATABASE_HOST=aws-0-[region].pooler.supabase.com

DATABASE_PORT=5432
   O BIEN (si usas connection pooling)
DATABASE_PORT=6543

DATABASE_NAME=postgres

DATABASE_USERNAME=postgres
   O BIEN
DATABASE_USERNAME=postgres.[tu-proyecto-ref]

DATABASE_PASSWORD=[la-contraseña-que-creaste-al-crear-el-proyecto]
```

### 2. Variable JWT (Seguridad)

```
JWT_SECRET=[genera-una-clave-segura-larga-de-al-menos-256-bits]
```

**Ejemplo de clave segura:**
```
JWT_SECRET=mySecretKeyForJWTTokenGenerationThatIsLongEnoughToMeetThe256BitRequirementForHMACSHA256Algorithm2024Production
```

### 3. Variable de Perfil de Spring (Opcional pero recomendado)

```
SPRING_PROFILES_ACTIVE=prod
```

---

## ✅ Verificación de Configuración

Después de agregar las variables, verifica:

1. ✅ Todas las variables tienen valores (sin espacios extra)
2. ✅ DATABASE_PASSWORD no tiene caracteres especiales que necesiten escape
3. ✅ DATABASE_HOST termina en `.supabase.co` o `.pooler.supabase.com`
4. ✅ DATABASE_PORT es `5432` o `6543`
5. ✅ JWT_SECRET es una cadena larga (mínimo 64 caracteres)

---

## 🔍 Cómo Obtener la Información de Supabase

### Paso a Paso:

1. **Abre tu proyecto en Supabase** (https://supabase.com)
2. **Ve al menú lateral**: Settings (⚙️) → Database
3. **Busca una de estas secciones**:
   - **Connection String** → modo "URI"
   - **Connection Pooling** (RECOMENDADO para Render)
   
4. **Anota los datos**:
   - Si ves una URI completa, úsala para extraer los valores
   - Si usas Connection Pooling, usa el puerto 6543
   - Si usas conexión directa, usa el puerto 5432

### Ejemplo de Connection String de Supabase:

```
postgresql://postgres.abcdefghijklmnop:tu-password-aqui@aws-0-us-east-1.pooler.supabase.com:6543/postgres
```

Se descompone en:
- **DATABASE_USERNAME**: `postgres.abcdefghijklmnop`
- **DATABASE_PASSWORD**: `tu-password-aqui`
- **DATABASE_HOST**: `aws-0-us-east-1.pooler.supabase.com`
- **DATABASE_PORT**: `6543`
- **DATABASE_NAME**: `postgres`

---

## 🚀 Después de Configurar

1. **Guarda** las variables de entorno en Render
2. **Re-deploy** tu aplicación (Render debería hacerlo automáticamente)
3. **Revisa los logs** para verificar que la conexión sea exitosa
4. **Busca en los logs**: `"Started AlquigestApplication"` para confirmar que arrancó correctamente

---

## 🐛 Troubleshooting

### Si ves "Network unreachable":
- ✅ Verifica que DATABASE_HOST sea correcto
- ✅ Asegúrate de usar Connection Pooling (puerto 6543) en lugar de conexión directa
- ✅ Verifica que no haya espacios en las variables de entorno

### Si ves "Authentication failed":
- ✅ Verifica DATABASE_PASSWORD
- ✅ Verifica DATABASE_USERNAME (incluye el sufijo del proyecto si es necesario)

### Si ves "database does not exist":
- ✅ Verifica que DATABASE_NAME sea "postgres" (en minúsculas)

---

## 📝 Checklist Final

Antes de hacer deploy, verifica:

- [ ] He creado el proyecto en Supabase
- [ ] He copiado las credenciales correctamente
- [ ] He configurado TODAS las variables de entorno en Render
- [ ] He verificado que no hay espacios extras en las variables
- [ ] He guardado los cambios en Render
- [ ] He hecho commit y push de los cambios al repositorio
- [ ] Render está configurado para usar el directorio `backend`
- [ ] Render está configurado para usar Docker como Environment


