package com.alquileres.service;

import com.alquileres.model.RolNombre;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PermisosService {

    /**
     * Obtiene los permisos para un rol específico
     * Los permisos siguen el formato: accion_sujeto (ej: crear_propietario)
     * Acciones: crear, modificar, consultar, eliminar
     * Sujetos: propietario, inmueble, inquilino
     */
    public Map<String, Boolean> obtenerPermisosPorRol(RolNombre rol) {
        Map<String, Boolean> permisos = new HashMap<>();

        // Inicializar todos los permisos en false
        String[] sujetos = {"propietario", "inmueble", "inquilino"};
        String[] acciones = {"crear", "consultar", "modificar", "eliminar"};

        for (String sujeto : sujetos) {
            for (String accion : acciones) {
                String permisoKey = accion + "_" + sujeto;
                permisos.put(permisoKey, false);
            }
        }

        // Asignar permisos específicos según el rol
        switch (rol) {
            case ROLE_ADMINISTRADOR:
                // El administrador tiene todos los permisos
                permisos.replaceAll((key, value) -> true);
                break;

            case ROLE_ABOGADA:
                // La abogada puede consultar todo, crear y modificar propietarios e inquilinos
                // Consultar todos
                permisos.put("consultar_propietario", true);
                permisos.put("consultar_inmueble", true);
                permisos.put("consultar_inquilino", true);

                // Crear y modificar propietarios e inquilinos
                permisos.put("crear_propietario", true);
                permisos.put("modificar_propietario", true);
                permisos.put("crear_inquilino", true);
                permisos.put("modificar_inquilino", true);

                // Crear y modificar inmuebles
                permisos.put("crear_inmueble", true);
                permisos.put("modificar_inmueble", true);
                break;

            case ROLE_SECRETARIA:
                // La secretaria puede consultar todo y crear/modificar con limitaciones
                // Consultar todos
                permisos.put("consultar_propietario", true);
                permisos.put("consultar_inmueble", true);
                permisos.put("consultar_inquilino", true);

                // Crear propietarios e inquilinos
                permisos.put("crear_propietario", true);
                permisos.put("crear_inquilino", true);

                // Modificar solo inquilinos
                permisos.put("modificar_inquilino", true);
                break;
        }

        return permisos;
    }

    /**
     * Obtiene los permisos consolidados para múltiples roles
     * Si el usuario tiene múltiples roles, se aplica OR lógico (cualquier rol que tenga el permiso lo otorga)
     */
    public Map<String, Boolean> obtenerPermisosConsolidados(Iterable<RolNombre> roles) {
        Map<String, Boolean> permisosConsolidados = new HashMap<>();

        // Inicializar todos los permisos en false
        String[] sujetos = {"propietario", "inmueble", "inquilino"};
        String[] acciones = {"crear", "consultar", "modificar", "eliminar"};

        for (String sujeto : sujetos) {
            for (String accion : acciones) {
                String permisoKey = accion + "_" + sujeto;
                permisosConsolidados.put(permisoKey, false);
            }
        }

        // Aplicar OR lógico para cada rol
        for (RolNombre rol : roles) {
            Map<String, Boolean> permisosRol = obtenerPermisosPorRol(rol);

            for (Map.Entry<String, Boolean> entry : permisosRol.entrySet()) {
                String permiso = entry.getKey();
                Boolean tienePermiso = entry.getValue();

                // Si algún rol tiene el permiso, se otorga
                if (tienePermiso) {
                    permisosConsolidados.put(permiso, true);
                }
            }
        }

        return permisosConsolidados;
    }
}
