package com.alquileres.repository;

import com.alquileres.model.ServicioXContrato;
import com.alquileres.model.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioXContratoRepository extends JpaRepository<ServicioXContrato, Integer> {

    // Buscar por contrato
    List<ServicioXContrato> findByContrato(Contrato contrato);

    // Buscar por ID de contrato
    @Query("SELECT s FROM ServicioXContrato s WHERE s.contrato.id = :contratoId")
    List<ServicioXContrato> findByContratoId(@Param("contratoId") Long contratoId);

    // Buscar servicios activos por contrato
    @Query("SELECT s FROM ServicioXContrato s WHERE s.contrato.id = :contratoId AND s.esActivo = true")
    List<ServicioXContrato> findServiciosActivosByContratoId(@Param("contratoId") Long contratoId);

    // Buscar por es activo
    List<ServicioXContrato> findByEsActivo(Boolean esActivo);

    // Buscar por es de inquilino
    List<ServicioXContrato> findByEsDeInquilino(Boolean esDeInquilino);

    // Buscar por tipo de servicio
    @Query("SELECT s FROM ServicioXContrato s WHERE s.tipoServicio.id = :tipoServicioId")
    List<ServicioXContrato> findByTipoServicioId(@Param("tipoServicioId") Integer tipoServicioId);
}

