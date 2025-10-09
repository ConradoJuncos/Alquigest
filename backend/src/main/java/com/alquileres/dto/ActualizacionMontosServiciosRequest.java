package com.alquileres.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ActualizacionMontosServiciosRequest {

    @NotNull(message = "El ID del contrato es obligatorio")
    private Long contratoId;

    @NotEmpty(message = "La lista de actualizaciones no puede estar vacía")
    @Valid
    private List<ActualizacionMontoServicioDTO> actualizaciones;

    public ActualizacionMontosServiciosRequest() {
    }

    public ActualizacionMontosServiciosRequest(Long contratoId, List<ActualizacionMontoServicioDTO> actualizaciones) {
        this.contratoId = contratoId;
        this.actualizaciones = actualizaciones;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public List<ActualizacionMontoServicioDTO> getActualizaciones() {
        return actualizaciones;
    }

    public void setActualizaciones(List<ActualizacionMontoServicioDTO> actualizaciones) {
        this.actualizaciones = actualizaciones;
    }

    @Override
    public String toString() {
        return "ActualizacionMontosServiciosRequest{" +
                "contratoId=" + contratoId +
                ", actualizaciones=" + actualizaciones +
                '}';
    }
}

