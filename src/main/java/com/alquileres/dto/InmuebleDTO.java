package com.alquileres.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class InmuebleDTO {

    private Long id;

    @NotNull(message = "El propietario es obligatorio")
    private Long propietarioId;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    private Integer tipoInmuebleId;

    private String tipo;

    @NotNull(message = "El estado es obligatorio")
    private Integer estado;

    @PositiveOrZero(message = "La superficie debe ser positiva o cero")
    private BigDecimal superficie;

    private Boolean esAlquilado;
    private Boolean esActivo;
    private String createdAt;
    private String updatedAt;

    // Constructor por defecto
    public InmuebleDTO() {
    }

    // Constructor completo
    public InmuebleDTO(Long id, Long propietarioId, String direccion, Integer tipoInmuebleId,
                      String tipo, Integer estado, BigDecimal superficie, Boolean esAlquilado,
                      Boolean esActivo, String createdAt, String updatedAt) {
        this.id = id;
        this.propietarioId = propietarioId;
        this.direccion = direccion;
        this.tipoInmuebleId = tipoInmuebleId;
        this.tipo = tipo;
        this.estado = estado;
        this.superficie = superficie;
        this.esAlquilado = esAlquilado;
        this.esActivo = esActivo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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
