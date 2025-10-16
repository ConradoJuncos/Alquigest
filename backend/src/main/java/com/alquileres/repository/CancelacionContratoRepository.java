package com.alquileres.repository;

import com.alquileres.model.CancelacionContrato;
import com.alquileres.model.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CancelacionContratoRepository extends JpaRepository<CancelacionContrato, Long> {

    // Buscar todas con JOIN FETCH para cargar las relaciones
    @Query("SELECT c FROM CancelacionContrato c " +
           "JOIN FETCH c.contrato " +
           "JOIN FETCH c.motivoCancelacion")
    List<CancelacionContrato> findAllWithRelations();

    // Buscar por ID con JOIN FETCH
    @Query("SELECT c FROM CancelacionContrato c " +
           "JOIN FETCH c.contrato " +
           "JOIN FETCH c.motivoCancelacion " +
           "WHERE c.id = :id")
    Optional<CancelacionContrato> findByIdWithRelations(@Param("id") Long id);

    // Buscar por contrato
    Optional<CancelacionContrato> findByContrato(Contrato contrato);

    // Buscar por ID de contrato con JOIN FETCH
    @Query("SELECT c FROM CancelacionContrato c " +
           "JOIN FETCH c.contrato " +
           "JOIN FETCH c.motivoCancelacion " +
           "WHERE c.contrato.id = :contratoId")
    Optional<CancelacionContrato> findByContratoIdWithRelations(@Param("contratoId") Long contratoId);

    // Buscar por ID de contrato (sin fetch)
    @Query("SELECT c FROM CancelacionContrato c WHERE c.contrato.id = :contratoId")
    Optional<CancelacionContrato> findByContratoId(@Param("contratoId") Long contratoId);

    // Verificar si existe una cancelación para un contrato
    boolean existsByContrato(Contrato contrato);

    // Verificar si existe una cancelación para un contrato por ID
    @Query("SELECT COUNT(c) > 0 FROM CancelacionContrato c WHERE c.contrato.id = :contratoId")
    boolean existsByContratoId(@Param("contratoId") Long contratoId);

    // Buscar todas las cancelaciones por motivo con JOIN FETCH
    @Query("SELECT c FROM CancelacionContrato c " +
           "JOIN FETCH c.contrato " +
           "JOIN FETCH c.motivoCancelacion " +
           "WHERE c.motivoCancelacion.id = :motivoId")
    List<CancelacionContrato> findByMotivoCancelacionIdWithRelations(@Param("motivoId") Integer motivoId);

    // Buscar todas las cancelaciones por motivo (sin fetch)
    @Query("SELECT c FROM CancelacionContrato c WHERE c.motivoCancelacion.id = :motivoId")
    List<CancelacionContrato> findByMotivoCancelacionId(@Param("motivoId") Integer motivoId);

    // Buscar cancelaciones en un rango de fechas
    @Query("SELECT c FROM CancelacionContrato c WHERE c.fechaCancelacion >= :fechaInicio AND c.fechaCancelacion <= :fechaFin")
    List<CancelacionContrato> findByFechaCancelacionBetween(@Param("fechaInicio") String fechaInicio, @Param("fechaFin") String fechaFin);

    // Contar cancelaciones por motivo
    @Query("SELECT COUNT(c) FROM CancelacionContrato c WHERE c.motivoCancelacion.id = :motivoId")
    Long countByMotivoCancelacionId(@Param("motivoId") Integer motivoId);
}
