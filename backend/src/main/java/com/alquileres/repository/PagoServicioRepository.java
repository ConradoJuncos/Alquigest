package com.alquileres.repository;

import com.alquileres.model.PagoServicio;
import com.alquileres.model.ServicioXInmueble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoServicioRepository extends JpaRepository<PagoServicio, Integer> {

    // Buscar por servicio x inmueble
    List<PagoServicio> findByServicioXInmueble(ServicioXInmueble servicioXInmueble);

    // Buscar por ID de servicio x inmueble
    @Query("SELECT p FROM PagoServicio p WHERE p.servicioXInmueble.id = :servicioXInmuebleId")
    List<PagoServicio> findByServicioXInmuebleId(@Param("servicioXInmuebleId") Integer servicioXInmuebleId);

    // Buscar por está pagado
    List<PagoServicio> findByEstaPagado(Boolean estaPagado);

    // Buscar por está vencido
    List<PagoServicio> findByEstaVencido(Boolean estaVencido);

    // Buscar pagos pendientes (no pagados)
    @Query("SELECT p FROM PagoServicio p WHERE p.estaPagado = false")
    List<PagoServicio> findPagosPendientes();

    // Buscar pagos vencidos
    @Query("SELECT p FROM PagoServicio p WHERE p.estaVencido = true AND p.estaPagado = false")
    List<PagoServicio> findPagosVencidos();

    // Buscar pagos por inmueble
    @Query("SELECT p FROM PagoServicio p WHERE p.servicioXInmueble.inmueble.id = :inmuebleId")
    List<PagoServicio> findByInmuebleId(@Param("inmuebleId") Long inmuebleId);

    // Contar pagos pendientes
    @Query("SELECT COUNT(p) FROM PagoServicio p WHERE p.estaPagado = false")
    Long countPagosPendientes();

    // Contar pagos vencidos
    @Query("SELECT COUNT(p) FROM PagoServicio p WHERE p.estaVencido = true AND p.estaPagado = false")
    Long countPagosVencidos();
}