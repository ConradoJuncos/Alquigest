package com.alquileres.dto;

import com.alquileres.model.ServicioXContrato;

public class ServicioXContratoResponseDTO {

    private Integer id;
    private Long contratoId;
    private TipoServicioDTO tipoServicio;
    private String nroCuenta;
    private String nroContrato;
    private Boolean esDeInquilino;
    private Boolean esAnual;
    private Boolean esActivo;

    public ServicioXContratoResponseDTO() {
    }

    public ServicioXContratoResponseDTO(ServicioXContrato servicio) {
        this.id = servicio.getId();
        this.contratoId = servicio.getContrato() != null ? servicio.getContrato().getId() : null;
        if (servicio.getTipoServicio() != null) {
            this.tipoServicio = new TipoServicioDTO(servicio.getTipoServicio().getId(), servicio.getTipoServicio().getNombre());
        }
        this.nroCuenta = servicio.getNroCuenta();
        this.nroContrato = servicio.getNroContrato();
        this.esDeInquilino = servicio.getEsDeInquilino();
        this.esAnual = servicio.getEsAnual();
        this.esActivo = servicio.getEsActivo();
    }

    // Getters y setters
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

    public TipoServicioDTO getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicioDTO tipoServicio) {
        this.tipoServicio = tipoServicio;
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

    // DTO anidado para tipoServicio
    public static class TipoServicioDTO {
        private Integer id;
        private String nombre;

        public TipoServicioDTO() {
        }

        public TipoServicioDTO(Integer id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }
}

