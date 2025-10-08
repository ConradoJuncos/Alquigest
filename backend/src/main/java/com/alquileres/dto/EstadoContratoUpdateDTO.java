package com.alquileres.dto;

import jakarta.validation.constraints.NotNull;

public class EstadoContratoUpdateDTO {

    @NotNull(message = "El ID del estado es obligatorio")
    private Integer estadoContratoId;

    private Integer motivoCancelacionId;

    public EstadoContratoUpdateDTO() {}

    public EstadoContratoUpdateDTO(Integer estadoContratoId) {
        this.estadoContratoId = estadoContratoId;
    }

    public EstadoContratoUpdateDTO(Integer estadoContratoId, Integer motivoCancelacionId) {
        this.estadoContratoId = estadoContratoId;
        this.motivoCancelacionId = motivoCancelacionId;
    }

    public Integer getEstadoContratoId() {
        return estadoContratoId;
    }

    public void setEstadoContratoId(Integer estadoContratoId) {
        this.estadoContratoId = estadoContratoId;
    }

    public Integer getMotivoCancelacionId() {
        return motivoCancelacionId;
    }

    public void setMotivoCancelacionId(Integer motivoCancelacionId) {
        this.motivoCancelacionId = motivoCancelacionId;
    }
}
