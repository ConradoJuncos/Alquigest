package com.alquileres.repository;

import com.alquileres.model.Inmueble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InmuebleRepository extends JpaRepository<Inmueble, Long> {

    // Buscar inmuebles activos
    List<Inmueble> findByEsActivoTrue();

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
}
