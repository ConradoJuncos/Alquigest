package com.alquileres.repository;

import com.alquileres.model.MotivoCancelacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MotivoCancelacionRepository extends JpaRepository<MotivoCancelacion, Integer> {

    // Buscar por nombre
    Optional<MotivoCancelacion> findByNombre(String nombre);

    // Verificar si existe por nombre
    boolean existsByNombre(String nombre);
}

