package com.alquileres.repository;

import com.alquileres.model.Alquiler;
import com.alquileres.model.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {

    // Buscar alquileres por contrato
    List<Alquiler> findByContrato(Contrato contrato);

    // Buscar alquileres por contrato ID
    @Query("SELECT a FROM Alquiler a WHERE a.contrato.id = :contratoId")
    List<Alquiler> findByContratoId(@Param("contratoId") Long contratoId);

    // Buscar alquileres pagados
    List<Alquiler> findByEstaPagado(Boolean estaPagado);

    // Buscar alquileres pendientes de pago por contrato
    @Query("SELECT a FROM Alquiler a WHERE a.contrato.id = :contratoId AND a.estaPagado = false")
    List<Alquiler> findAlquileresPendientesByContratoId(@Param("contratoId") Long contratoId);

    // Buscar alquileres pagados por contrato
    @Query("SELECT a FROM Alquiler a WHERE a.contrato.id = :contratoId AND a.estaPagado = true")
    List<Alquiler> findAlquileresPagadosByContratoId(@Param("contratoId") Long contratoId);

    // Contar alquileres pendientes de pago
    @Query("SELECT COUNT(a) FROM Alquiler a WHERE a.estaPagado = false")
    Long countAlquileresPendientes();

    // Buscar alquileres con vencimiento próximo
    @Query("SELECT a FROM Alquiler a WHERE a.estaPagado = false AND a.fechaVencimientoPago BETWEEN :fechaActual AND :fechaLimite")
    List<Alquiler> findAlquileresProximosAVencer(@Param("fechaActual") String fechaActual, @Param("fechaLimite") String fechaLimite);

    // Contar alquileres con vencimiento próximo
    @Query("SELECT COUNT(a) FROM Alquiler a WHERE a.estaPagado = false AND a.fechaVencimientoPago BETWEEN :fechaActual AND :fechaLimite")
    Long countAlquileresProximosAVencer(@Param("fechaActual") String fechaActual, @Param("fechaLimite") String fechaLimite);

    // Buscar alquileres del mes actual (de contratos vigentes)
    @Query("SELECT a FROM Alquiler a WHERE YEAR(CAST(a.fechaVencimientoPago AS date)) = YEAR(CURRENT_DATE) AND MONTH(CAST(a.fechaVencimientoPago AS date)) = MONTH(CURRENT_DATE)")
    List<Alquiler> findAlquileresDelMes();

    // Buscar alquileres no pagados del mes actual con sus datos asociados
    @Query("SELECT a FROM Alquiler a WHERE a.estaPagado = false AND YEAR(CAST(a.fechaVencimientoPago AS date)) = YEAR(CURRENT_DATE) AND MONTH(CAST(a.fechaVencimientoPago AS date)) = MONTH(CURRENT_DATE)")
    List<Alquiler> findAlquileresNoPagadosDelMes();

    // Obtener el último alquiler de un contrato (ordenado por fecha de vencimiento descendente)
    @Query("SELECT a FROM Alquiler a WHERE a.contrato.id = :contratoId ORDER BY CAST(a.fechaVencimientoPago AS date) DESC LIMIT 1")
    Optional<Alquiler> findUltimoAlquilerByContratoId(@Param("contratoId") Long contratoId);
}

