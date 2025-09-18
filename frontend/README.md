# Alquigest Frontend

Interfaz de usuario para el sistema de gestión de alquileres.

## Estado actual

📂 Módulos / Funcionalidades disponibles

- Inmuebles
  
  - Carga de nuevo inmueble, modificación y consulta   

  - Registro de dirección, tipo, estado, superficie y propietario.

- Propietarios

    - Alta de nuevo propietario, modificación de datos, baja lógica y consulta. 

    - Datos personales (nombre, apellido, DNI, etc.).


## 🚀 Tecnologías principales

- [Next.js 14](https://nextjs.org/) – Framework de React para renderizado híbrido (SSR/SSG).
- [TypeScript](https://www.typescriptlang.org/) – Tipado estático y mayor robustez.
- [TailwindCSS](https://tailwindcss.com/) – Estilos rápidos y personalizables.
- [Radix UI](https://www.radix-ui.com/) – Componentes accesibles de bajo nivel.
- [lucide-react](https://lucide.dev/) – Iconos personalizables.
- [react-hook-form](https://react-hook-form.com/) + [zod](https://zod.dev/) – Manejo y validación de formularios.

---

## ⚙️ Requisitos previos

Asegúrate de tener instalado en tu máquina:

- [Node.js](https://nodejs.org/) **>=18**
- [npm](https://www.npmjs.com/) **>=9**

---

## Conexión con Backend

El frontend deberá consumir la API REST del backend que se ejecuta en:
- **URL Base**: `http://localhost:8081`
- **Documentación**: `http://localhost:8081/swagger-ui.html`

## Próximos pasos

1. Elegir el framework/tecnología frontend
2. Configurar el entorno de desarrollo
3. Implementar las interfaces para gestión de inmuebles y propietarios
4. Integrar con la API del backend

---

## 📦 Instalación y ejecución

Clona el repositorio e instala las dependencias:

```bash
git clone <url-del-repo>
cd alquigest-frontend
npm install
npm run dev
```

Para construir el proyecto para producción:

```bash
npm run build
npm start
```