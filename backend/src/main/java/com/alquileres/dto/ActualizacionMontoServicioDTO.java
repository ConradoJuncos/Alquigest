package com.alquileres.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * DTO para actualizar el monto de un tipo de servicio específico
 */
public class ActualizacionMontoServicioDTO {

    @NotNull(message = "El ID del tipo de servicio es obligatorio")
    private Integer tipoServicioId;

    @Positive(message = "El nuevo monto debe ser positivo")
    private BigDecimal nuevoMonto;

    // Constructores
    public ActualizacionMontoServicioDTO() {
    }

    public ActualizacionMontoServicioDTO(Integer tipoServicioId, BigDecimal nuevoMonto) {
        this.tipoServicioId = tipoServicioId;
        this.nuevoMonto = nuevoMonto;
    }

    // Getters y Setters
    public Integer getTipoServicioId() {
        return tipoServicioId;
    }

    public void setTipoServicioId(Integer tipoServicioId) {
        this.tipoServicioId = tipoServicioId;
    }

    public BigDecimal getNuevoMonto() {
        return nuevoMonto;
    }

    public void setNuevoMonto(BigDecimal nuevoMonto) {
        this.nuevoMonto = nuevoMonto;
    }

    @Override
    public String toString() {
        return "ActualizacionMontoServicioDTO{" +
                "tipoServicioId=" + tipoServicioId +
                ", nuevoMonto=" + nuevoMonto +
                '}';
    }
}

