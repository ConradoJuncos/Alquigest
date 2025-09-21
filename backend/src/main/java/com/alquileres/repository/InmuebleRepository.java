package com.alquileres.repository;

import com.alquileres.model.Inmueble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InmuebleRepository extends JpaRepository<Inmueble, Long> {

    // Buscar inmuebles activos
    List<Inmueble> findByEsActivoTrue();

    // Buscar inmuebles inactivos
    List<Inmueble> findByEsActivoFalse();

    // Buscar inmuebles disponibles (no alquilados y activos)
    List<Inmueble> findByEsAlquiladoFalseAndEsActivoTrue();

    // Buscar por propietario
    List<Inmueble> findByPropietarioId(Long propietarioId);

    // Buscar por dirección (búsqueda parcial)
    List<Inmueble> findByDireccionContainingIgnoreCase(String direccion);

    // Buscar por tipo (búsqueda parcial)
    List<Inmueble> findByTipoContainingIgnoreCase(String tipo);

    // Buscar por estado
    List<Inmueble> findByEstado(Integer estado);

    // Buscar inmuebles alquilados
    List<Inmueble> findByEsAlquiladoTrue();

    // Desactivar todos los inmuebles de un propietario
    @Modifying
    @Query("UPDATE Inmueble i SET i.esActivo = false WHERE i.propietarioId = :propietarioId")
    void desactivarInmueblesPorPropietario(@Param("propietarioId") Long propietarioId);

    // Solo útil en caso que se use el endpoint PATCH para activar al propietario
    @Modifying
    @Query("UPDATE Inmueble i SET i.esActivo = true WHERE i.propietarioId = :propietarioId")
    void activarInmueblesPorPropietario(@Param("propietarioId") Long propietarioId);
}

// En caso de que no sea facil implementar el endpoint PATCH, podemos usar esta query en el PUT
// @Query("UPDATE Inmueble i SET i.esActivo = (SELECT p.esActivo FROM Propietario p WHERE p.id = i.propietarioId) WHERE i.propietarioId = :propietarioId")