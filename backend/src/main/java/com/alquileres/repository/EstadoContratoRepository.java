package com.alquileres.repository;

import com.alquileres.model.EstadoContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoContratoRepository extends JpaRepository<EstadoContrato, Integer> {

    // Buscar por nombre
    Optional<EstadoContrato> findByNombre(String nombre);

    // Verificar si existe por nombre
    boolean existsByNombre(String nombre);
}
