package com.alquileres.repository;

import com.alquileres.model.ServicioXInmueble;
import com.alquileres.model.Inmueble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioXInmuebleRepository extends JpaRepository<ServicioXInmueble, Integer> {

    // Buscar por inmueble
    List<ServicioXInmueble> findByInmueble(Inmueble inmueble);

    // Buscar por ID de inmueble
    @Query("SELECT s FROM ServicioXInmueble s WHERE s.inmueble.id = :inmuebleId")
    List<ServicioXInmueble> findByInmuebleId(@Param("inmuebleId") Long inmuebleId);

    // Buscar servicios activos por inmueble
    @Query("SELECT s FROM ServicioXInmueble s WHERE s.inmueble.id = :inmuebleId AND s.esActivo = true")
    List<ServicioXInmueble> findServiciosActivosByInmuebleId(@Param("inmuebleId") Long inmuebleId);

    // Buscar por es activo
    List<ServicioXInmueble> findByEsActivo(Boolean esActivo);

    // Buscar por es de inquilino
    List<ServicioXInmueble> findByEsDeInquilino(Boolean esDeInquilino);

    // Buscar por tipo de servicio
    @Query("SELECT s FROM ServicioXInmueble s WHERE s.tipoServicio.id = :tipoServicioId")
    List<ServicioXInmueble> findByTipoServicioId(@Param("tipoServicioId") Integer tipoServicioId);
}

