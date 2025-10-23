package com.alquileres.repository;

import com.alquileres.model.AmbitoPDF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AmbitoPDFRepository extends JpaRepository<AmbitoPDF, Long> {
    Optional<AmbitoPDF> findByNombre(String nombre);
}

