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

    // Buscar por nombre (contiene e ignora mayúsculas/minúsculas)
    List<Inquilino> findByNombreContainingIgnoreCase(String nombre);

    // Verificar si existe CUIL (para validaciones de unicidad)
    boolean existsByCuil(String cuil);

    // Verificar si existe CUIL excluyendo un ID específico (para actualizaciones)
    boolean existsByCuilAndIdNot(String cuil, Long id);
}
