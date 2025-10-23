package com.alquileres.repository;

import com.alquileres.model.PDF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PDFRepository extends JpaRepository<PDF, Long> {
}

