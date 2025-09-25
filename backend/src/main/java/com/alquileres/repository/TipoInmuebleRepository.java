package com.alquileres.repository;

import com.alquileres.model.TipoInmueble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoInmuebleRepository extends JpaRepository<TipoInmueble, Long> {

    Optional<TipoInmueble> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
