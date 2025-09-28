package com.alquileres.dto;

import jakarta.validation.constraints.NotNull;

public class EstadoContratoUpdateDTO {

    @NotNull(message = "El ID del estado es obligatorio")
    private Integer estadoContratoId;

    public EstadoContratoUpdateDTO() {}

    public EstadoContratoUpdateDTO(Integer estadoContratoId) {
        this.estadoContratoId = estadoContratoId;
    }

    public Integer getEstadoContratoId() {
        return estadoContratoId;
    }

    public void setEstadoContratoId(Integer estadoContratoId) {
        this.estadoContratoId = estadoContratoId;
    }
}
