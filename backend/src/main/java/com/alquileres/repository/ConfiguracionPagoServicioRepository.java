package com.alquileres.repository;

import com.alquileres.model.ConfiguracionPagoServicio;
import com.alquileres.model.ServicioXInmueble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracionPagoServicioRepository extends JpaRepository<ConfiguracionPagoServicio, Integer> {

    // Buscar por servicio x inmueble
    Optional<ConfiguracionPagoServicio> findByServicioXInmueble(ServicioXInmueble servicioXInmueble);

    // Buscar por ID de servicio x inmueble
    @Query("SELECT c FROM ConfiguracionPagoServicio c WHERE c.servicioXInmueble.id = :servicioXInmuebleId")
    Optional<ConfiguracionPagoServicio> findByServicioXInmuebleId(@Param("servicioXInmuebleId") Integer servicioXInmuebleId);

    // Buscar configuraciones activas
    List<ConfiguracionPagoServicio> findByEsActivo(Boolean esActivo);

    // Buscar configuraciones con pagos pendientes de generar (proximoPago <= fecha actual)
    @Query("SELECT c FROM ConfiguracionPagoServicio c WHERE c.esActivo = true AND c.proximoPago <= :fechaActual")
    List<ConfiguracionPagoServicio> findConfiguracionesConPagosPendientes(@Param("fechaActual") String fechaActual);

    // Buscar configuraciones por inmueble
    @Query("SELECT c FROM ConfiguracionPagoServicio c WHERE c.servicioXInmueble.inmueble.id = :inmuebleId")
    List<ConfiguracionPagoServicio> findByInmuebleId(@Param("inmuebleId") Long inmuebleId);

    // Verificar si existe configuración para un servicio x inmueble
    @Query("SELECT COUNT(c) > 0 FROM ConfiguracionPagoServicio c WHERE c.servicioXInmueble.id = :servicioXInmuebleId")
    boolean existsByServicioXInmuebleId(@Param("servicioXInmuebleId") Integer servicioXInmuebleId);
}

