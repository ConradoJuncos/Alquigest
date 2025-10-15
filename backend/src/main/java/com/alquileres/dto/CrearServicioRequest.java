package com.alquileres.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para crear un nuevo servicio asociado a un contrato
 */
public class CrearServicioRequest {

    @NotNull(message = "El ID del contrato es obligatorio")
    private Long contratoId;

    @NotNull(message = "El ID del tipo de servicio es obligatorio")
    private Integer tipoServicioId;

    private String nroCuenta;
    private String nroContrato;
    private Boolean esDeInquilino = false;
    private Boolean esAnual = false; // false = mensual, true = anual
    private String fechaInicio; // Formato ISO (yyyy-MM-dd)

    // Constructores
    public CrearServicioRequest() {
    }

    public CrearServicioRequest(Long contratoId, Integer tipoServicioId) {
        this.contratoId = contratoId;
        this.tipoServicioId = tipoServicioId;
    }

    // Getters y Setters
    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public Integer getTipoServicioId() {
        return tipoServicioId;
    }

    public void setTipoServicioId(Integer tipoServicioId) {
        this.tipoServicioId = tipoServicioId;
    }

    public String getNroCuenta() {
        return nroCuenta;
    }

    public void setNroCuenta(String nroCuenta) {
        this.nroCuenta = nroCuenta;
    }

    public String getNroContrato() {
        return nroContrato;
    }

    public void setNroContrato(String nroContrato) {
        this.nroContrato = nroContrato;
    }

    public Boolean getEsDeInquilino() {
        return esDeInquilino;
    }

    public void setEsDeInquilino(Boolean esDeInquilino) {
        this.esDeInquilino = esDeInquilino;
    }

    public Boolean getEsAnual() {
        return esAnual;
    }

    public void setEsAnual(Boolean esAnual) {
        this.esAnual = esAnual;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    @Override
    public String toString() {
        return "CrearServicioRequest{" +
                "contratoId=" + contratoId +
                ", tipoServicioId=" + tipoServicioId +
                ", nroCuenta='" + nroCuenta + '\'' +
                ", nroContrato='" + nroContrato + '\'' +
                ", esDeInquilino=" + esDeInquilino +
                ", esAnual=" + esAnual +
                ", fechaInicio='" + fechaInicio + '\'' +
                '}';
    }
}

