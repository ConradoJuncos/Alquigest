package com.alquileres.repository;

import com.alquileres.model.Propiedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {

    // Buscar propiedades disponibles
    List<Propiedad> findByDisponibleTrue();

    // Buscar propiedades por rango de precio
    List<Propiedad> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);

    // Buscar propiedades que contengan texto en título o descripción
    @Query("SELECT p FROM Propiedad p WHERE " +
           "LOWER(p.titulo) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Propiedad> buscarPorTexto(@Param("texto") String texto);

    // Buscar propiedades disponibles en un rango de precio
    List<Propiedad> findByDisponibleTrueAndPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);
}
