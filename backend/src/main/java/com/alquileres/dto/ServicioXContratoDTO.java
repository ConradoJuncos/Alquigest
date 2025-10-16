package com.alquileres.dto;

import com.alquileres.model.ServicioXContrato;

public class ServicioXContratoDTO {
    private Integer id;
    private Long contratoId;
    private Integer tipoServicioId;
    private String tipoServicioNombre;
    private String nroCuenta;
    private String nroContrato;
    private Boolean esDeInquilino;
    private Boolean esAnual;
    private Boolean esActivo;

    public ServicioXContratoDTO() {
    }

    public ServicioXContratoDTO(ServicioXContrato servicio) {
        this.id = servicio.getId();
        this.contratoId = servicio.getContrato().getId();
        this.tipoServicioId = servicio.getTipoServicio().getId();
        this.tipoServicioNombre = servicio.getTipoServicio().getNombre();
        this.nroCuenta = servicio.getNroCuenta();
        this.nroContrato = servicio.getNroContrato();
        this.esDeInquilino = servicio.getEsDeInquilino();
        this.esAnual = servicio.getEsAnual();
        this.esActivo = servicio.getEsActivo();
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getTipoServicioNombre() {
        return tipoServicioNombre;
    }

    public void setTipoServicioNombre(String tipoServicioNombre) {
        this.tipoServicioNombre = tipoServicioNombre;
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

    public Boolean getEsActivo() {
        return esActivo;
    }

    public void setEsActivo(Boolean esActivo) {
        this.esActivo = esActivo;
    }
}

