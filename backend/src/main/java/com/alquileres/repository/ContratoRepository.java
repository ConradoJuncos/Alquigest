package com.alquileres.repository;

import com.alquileres.model.Contrato;
import com.alquileres.model.EstadoContrato;
import com.alquileres.model.Inmueble;
import com.alquileres.model.Inquilino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    // Buscar contratos por inmueble
    List<Contrato> findByInmueble(Inmueble inmueble);

    // Buscar contratos por inquilino
    List<Contrato> findByInquilino(Inquilino inquilino);

    // Buscar contratos por estado
    List<Contrato> findByEstadoContrato(EstadoContrato estadoContrato);

    // Buscar contratos vigentes (por ejemplo, estado 'Vigente')
    @Query("SELECT c FROM Contrato c WHERE c.estadoContrato.nombre = 'Vigente'")
    List<Contrato> findContratosVigentes();

    // Buscar contratos no vigentes (estados 'No Vigente' y 'Cancelado')
    @Query("SELECT c FROM Contrato c WHERE c.estadoContrato.nombre IN ('No Vigente', 'Cancelado')")
    List<Contrato> findContratosNoVigentes();

    // Buscar contratos que vencen en una fecha específica
    List<Contrato> findByFechaFin(String fechaFin);

    // Buscar contratos que vencen antes de una fecha (comparación de strings)
    @Query("SELECT c FROM Contrato c WHERE c.fechaFin < :fecha")
    List<Contrato> findByFechaFinBefore(@Param("fecha") String fecha);

    // Buscar contratos que vencen después de una fecha (comparación de strings)
    @Query("SELECT c FROM Contrato c WHERE c.fechaFin > :fecha")
    List<Contrato> findByFechaFinAfter(@Param("fecha") String fecha);

    // Buscar contratos por rango de fechas (comparación de strings)
    @Query("SELECT c FROM Contrato c WHERE c.fechaInicio >= :fechaInicio AND c.fechaInicio <= :fechaFin")
    List<Contrato> findByFechaInicioBetween(@Param("fechaInicio") String fechaInicio, @Param("fechaFin") String fechaFin);

    // Verificar si existe un contrato vigente para un inmueble específico
    @Query("SELECT COUNT(c) > 0 FROM Contrato c WHERE c.inmueble = :inmueble AND c.estadoContrato.nombre = 'Vigente'")
    boolean existsContratoVigenteByInmueble(@Param("inmueble") Inmueble inmueble);

    // Buscar contratos por inmueble y estado
    List<Contrato> findByInmuebleAndEstadoContrato(Inmueble inmueble, EstadoContrato estadoContrato);
}
