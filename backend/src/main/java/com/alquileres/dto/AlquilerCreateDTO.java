package com.alquileres.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AlquilerCreateDTO {

    @NotNull(message = "El contrato es obligatorio")
    private Long contratoId;

    // La fecha de vencimiento es opcional, si no se proporciona se usa el día 10 del mes
    private String fechaVencimientoPago;

    // Constructor vacío
    public AlquilerCreateDTO() {
    }

    // Constructor para creación automática
    public AlquilerCreateDTO(Long contratoId, String fechaVencimientoPago) {
        this.contratoId = contratoId;
        this.fechaVencimientoPago = fechaVencimientoPago;
    }

    // Getters y Setters
    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public String getFechaVencimientoPago() {
        return fechaVencimientoPago;
    }

    public void setFechaVencimientoPago(String fechaVencimientoPago) {
        this.fechaVencimientoPago = fechaVencimientoPago;
    }
}