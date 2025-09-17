package com.alquileres.repository;

import com.alquileres.model.Inquilino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InquilinoRepository extends JpaRepository<Inquilino, Long> {

    // Buscar inquilino por CUIL
    Optional<Inquilino> findByCuil(String cuil);

    // Buscar inquilinos por nombre (búsqueda parcial, insensible a mayúsculas)
    @Query("SELECT i FROM Inquilino i WHERE LOWER(i.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Inquilino> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    // Buscar inquilinos activos
    List<Inquilino> findByEsActivoTrue();

    // Buscar inquilinos inactivos
    List<Inquilino> findByEsActivoFalse();

    // Buscar inquilinos por teléfono
    Optional<Inquilino> findByTelefono(String telefono);

    // Contar inquilinos activos
    @Query("SELECT COUNT(i) FROM Inquilino i WHERE i.esActivo = true")
    Long countInquilinosActivos();

    // Verificar si existe un CUIL
    boolean existsByCuil(String cuil);

    // Verificar si existe un teléfono
    boolean existsByTelefono(String telefono);

    // Buscar inquilinos por estado activo
    List<Inquilino> findByEsActivo(Boolean esActivo);
}
