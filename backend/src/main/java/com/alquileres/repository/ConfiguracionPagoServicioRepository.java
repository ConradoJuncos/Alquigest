package com.alquileres.repository;

import com.alquileres.model.ConfiguracionPagoServicio;
import com.alquileres.model.ServicioXContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracionPagoServicioRepository extends JpaRepository<ConfiguracionPagoServicio, Integer> {

    // Buscar por servicio x contrato
    Optional<ConfiguracionPagoServicio> findByServicioXContrato(ServicioXContrato servicioXContrato);

    // Buscar por ID de servicio x contrato
    @Query("SELECT c FROM ConfiguracionPagoServicio c WHERE c.servicioXContrato.id = :servicioXContratoId")
    Optional<ConfiguracionPagoServicio> findByServicioXContratoId(@Param("servicioXContratoId") Integer servicioXContratoId);

    // Buscar configuraciones activas
    List<ConfiguracionPagoServicio> findByEsActivo(Boolean esActivo);

    // Buscar configuraciones con pagos pendientes de generar (proximoPago <= fecha actual)
    @Query("SELECT c FROM ConfiguracionPagoServicio c WHERE c.esActivo = true AND c.proximoPago <= :fechaActual")
    List<ConfiguracionPagoServicio> findConfiguracionesConPagosPendientes(@Param("fechaActual") String fechaActual);

    // Buscar configuraciones por contrato
    @Query("SELECT c FROM ConfiguracionPagoServicio c WHERE c.servicioXContrato.contrato.id = :contratoId")
    List<ConfiguracionPagoServicio> findByContratoId(@Param("contratoId") Long contratoId);

    // Verificar si existe configuración para un servicio x contrato
    @Query("SELECT COUNT(c) > 0 FROM ConfiguracionPagoServicio c WHERE c.servicioXContrato.id = :servicioXContratoId")
    boolean existsByServicioXContratoId(@Param("servicioXContratoId") Integer servicioXContratoId);
}
