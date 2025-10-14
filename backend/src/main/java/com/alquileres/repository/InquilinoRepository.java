package com.alquileres.repository;

import com.alquileres.model.Inquilino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InquilinoRepository extends JpaRepository<Inquilino, Long> {

    // Buscar por CUIL
    Optional<Inquilino> findByCuil(String cuil);

    // Buscar inquilinos activos
    List<Inquilino> findByEsActivoTrue();

    // Buscar inquilinos inactivos
    List<Inquilino> findByEsActivoFalse();

    // Contar inquilinos activos
    Long countByEsActivoTrue();

    // Buscar inquilinos que están alquilando
    List<Inquilino> findByEstaAlquilandoTrue();

    // Buscar inquilinos que no están alquilando
    List<Inquilino> findByEstaAlquilandoFalse();

    // Buscar por nombre (contiene e ignora mayúsculas/minúsculas)
    List<Inquilino> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por apellido (contiene e ignora mayúsculas/minúsculas)
    List<Inquilino> findByApellidoContainingIgnoreCase(String apellido);

    // Buscar por nombre y apellido (contiene e ignora mayúsculas/minúsculas)
    List<Inquilino> findByNombreContainingIgnoreCaseAndApellidoContainingIgnoreCase(String nombre, String apellido);

    // Buscar por nombre O apellido (contiene e ignora mayúsculas/minúsculas)
    List<Inquilino> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    // Verificar si existe CUIL (para validaciones de unicidad)
    boolean existsByCuil(String cuil);

    // Verificar si existe CUIL excluyendo un ID específico (para actualizaciones)
    boolean existsByCuilAndIdNot(String cuil, Long id);
}
