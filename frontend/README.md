# Alquigest Frontend

Interfaz de usuario para el sistema de gestión de alquileres.

## Estado actual

Esta carpeta está preparada para contener la aplicación frontend que se desarrollará posteriormente.

## Tecnologías sugeridas

- **React** + TypeScript
- **Vue.js** + TypeScript  
- **Angular**
- **HTML/CSS/JavaScript vanilla**

## Estructura sugerida

```
frontend/
├── src/
│   ├── components/     # Componentes reutilizables
│   ├── pages/         # Páginas principales
│   ├── services/      # Servicios para consumir API
│   ├── utils/         # Utilidades
│   └── assets/        # Recursos estáticos
├── public/            # Archivos públicos
├── package.json       # Dependencias (si se usa framework JS)
└── README.md
```

## Conexión con Backend

El frontend deberá consumir la API REST del backend que se ejecuta en:
- **URL Base**: `http://localhost:8081`
- **Documentación**: `http://localhost:8081/swagger-ui.html`

## Próximos pasos

1. Elegir el framework/tecnología frontend
2. Configurar el entorno de desarrollo
3. Implementar las interfaces para gestión de inmuebles y propietarios
4. Integrar con la API del backend
