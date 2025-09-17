package com.alquileres.repository;

import com.alquileres.model.Inmueble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InmuebleRepository extends JpaRepository<Inmueble, Long> {

    // Buscar inmuebles activos
    List<Inmueble> findByEsActivoTrue();

    // Buscar inmuebles alquilados
    List<Inmueble> findByEsAlquiladoTrue();

    // Buscar inmuebles disponibles (activos y no alquilados)
    List<Inmueble> findByEsActivoTrueAndEsAlquiladoFalse();

    // Buscar inmuebles por propietario
    List<Inmueble> findByPropietarioId(Long propietarioId);

    // Buscar inmuebles por estado
    List<Inmueble> findByEstado(Integer estado);

    // Buscar inmuebles por tipo
    List<Inmueble> findByTipo(String tipo);

    // Buscar inmuebles por tipo de inmueble ID
    List<Inmueble> findByTipoInmuebleId(Integer tipoInmuebleId);

    // Buscar inmuebles por rango de superficie
    List<Inmueble> findBySuperficieBetween(BigDecimal superficieMin, BigDecimal superficieMax);

    // Buscar inmuebles que contengan texto en dirección o tipo
    @Query("SELECT i FROM Inmueble i WHERE " +
           "LOWER(i.direccion) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(i.tipo) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Inmueble> buscarPorTexto(@Param("texto") String texto);

    // Buscar inmuebles activos por propietario
    List<Inmueble> findByPropietarioIdAndEsActivoTrue(Long propietarioId);

    // Buscar inmuebles disponibles por estado
    List<Inmueble> findByEstadoAndEsActivoTrueAndEsAlquiladoFalse(Integer estado);
}
