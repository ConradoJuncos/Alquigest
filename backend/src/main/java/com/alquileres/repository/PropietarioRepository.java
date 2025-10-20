package com.alquileres.repository;

import com.alquileres.model.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropietarioRepository extends JpaRepository<Propietario, Long> {

    // Buscar propietarios activos
    List<Propietario> findByEsActivoTrue();

    // Buscar propietarios inactivos
    List<Propietario> findByEsActivoFalse();

    // Contar propietarios activos
    Long countByEsActivoTrue();

    // Buscar por CUIL (único)
    Optional<Propietario> findByCuil(String cuil);

    // Buscar por email (único)
    Optional<Propietario> findByEmail(String email);

    // Buscar por nombre y apellido
    List<Propietario> findByNombreContainingIgnoreCaseAndApellidoContainingIgnoreCase(String nombre, String apellido);

    // Verificar si existe un CUIL diferente al ID actual
    @Query("SELECT COUNT(p) > 0 FROM Propietario p WHERE p.cuil = :cuil AND p.id != :id")
    boolean existsByCuilAndIdNot(@Param("cuil") String cuil, @Param("id") Long id);

    // Verificar si existe un email diferente al ID actual
    @Query("SELECT COUNT(p) > 0 FROM Propietario p WHERE p.email = :email AND p.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
}
