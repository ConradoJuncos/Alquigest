package com.alquileres.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "inmuebles")
public class Inmueble {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "El propietario es obligatorio")
    @Column(name = "propietario_id", nullable = false)
    private Long propietarioId;

    @NotBlank(message = "La dirección es obligatoria")
    @Column(nullable = false, length = 100)
    private String direccion;

    @Column(name = "tipo_inmueble_id")
    private Integer tipoInmuebleId;

    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private Integer estado;

    @PositiveOrZero(message = "La superficie debe ser positiva")
    @Column(precision = 10, scale = 2)
    private BigDecimal superficie;

    @Column(name = "es_alquilado", nullable = false)
    private Boolean esAlquilado = false;

    @Column(name = "es_activo", nullable = false)
    private Boolean esActivo = true;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    // Constructor por defecto
    public Inmueble() {
    }

    // Constructor con parámetros principales
    public Inmueble(Long propietarioId, String direccion, String tipo, Integer estado) {
        this.propietarioId = propietarioId;
        this.direccion = direccion;
        this.estado = estado;
        this.esAlquilado = false;
        this.esActivo = true;
    }

    @PrePersist
    protected void onCreate() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPropietarioId() {
        return propietarioId;
    }

    public void setPropietarioId(Long propietarioId) {
        this.propietarioId = propietarioId;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getTipoInmuebleId() {
        return tipoInmuebleId;
    }

    public void setTipoInmuebleId(Integer tipoInmuebleId) {
        this.tipoInmuebleId = tipoInmuebleId;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public BigDecimal getSuperficie() {
        return superficie;
    }

    public void setSuperficie(BigDecimal superficie) {
        this.superficie = superficie;
    }

    public Boolean getEsAlquilado() {
        return esAlquilado;
    }

    public void setEsAlquilado(Boolean esAlquilado) {
        this.esAlquilado = esAlquilado;
    }

    public Boolean getEsActivo() {
        return esActivo;
    }

    public void setEsActivo(Boolean esActivo) {
        this.esActivo = esActivo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
