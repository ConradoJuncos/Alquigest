package com.alquileres.repository;

import com.alquileres.model.ConfiguracionSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionSistemaRepository extends JpaRepository<ConfiguracionSistema, Integer> {

    // Buscar por clave
    Optional<ConfiguracionSistema> findByClave(String clave);

    // Verificar si existe una clave
    @Query("SELECT COUNT(c) > 0 FROM ConfiguracionSistema c WHERE c.clave = :clave")
    boolean existsByClave(@Param("clave") String clave);
}

