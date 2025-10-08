package com.alquileres.repository;

import com.alquileres.model.TipoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoServicioRepository extends JpaRepository<TipoServicio, Integer> {
    
    // Buscar por nombre
    Optional<TipoServicio> findByNombre(String nombre);
    
    // Verificar si existe por nombre
    boolean existsByNombre(String nombre);
}

