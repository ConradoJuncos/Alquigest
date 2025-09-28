package com.alquileres.repository;

import com.alquileres.model.EstadoInmueble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoInmuebleRepository extends JpaRepository<EstadoInmueble, Integer> {
    Optional<EstadoInmueble> findByNombre(String nombre);
}
