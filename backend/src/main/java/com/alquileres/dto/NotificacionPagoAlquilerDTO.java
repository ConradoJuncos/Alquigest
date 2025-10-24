package com.alquileres.dto;

public class NotificacionPagoAlquilerDTO {

    private Long idContrato;
    private Long idInmueble;
    private Long idInquilino;
    private String direccion;
    private String apellidoInquilino;
    private String nombreInquilino;

    // Constructor vacío
    public NotificacionPagoAlquilerDTO() {
    }

    // Constructor completo
    public NotificacionPagoAlquilerDTO(Long idContrato, Long idInmueble, Long idInquilino, String direccion, String apellidoInquilino, String nombreInquilino) {
        this.idContrato = idContrato;
        this.idInmueble = idInmueble;
        this.idInquilino = idInquilino;
        this.direccion = direccion;
        this.apellidoInquilino = apellidoInquilino;
        this.nombreInquilino = nombreInquilino;
    }

    // Getters y Setters
    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public Long getIdInmueble() {
        return idInmueble;
    }

    public void setIdInmueble(Long idInmueble) {
        this.idInmueble = idInmueble;
    }

    public Long getIdInquilino() {
        return idInquilino;
    }

    public void setIdInquilino(Long idInquilino) {
        this.idInquilino = idInquilino;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getApellidoInquilino() {
        return apellidoInquilino;
    }

    public void setApellidoInquilino(String apellidoInquilino) {
        this.apellidoInquilino = apellidoInquilino;
    }

    public String getNombreInquilino() {
        return nombreInquilino;
    }

    public void setNombreInquilino(String nombreInquilino) {
        this.nombreInquilino = nombreInquilino;
    }

    @Override
    public String toString() {
        return "NotificacionPagoAlquilerDTO{" +
                "idContrato=" + idContrato +
                ", idInmueble=" + idInmueble +
                ", idInquilino=" + idInquilino +
                ", direccion='" + direccion + '\'' +
                ", apellidoInquilino='" + apellidoInquilino + '\'' +
                ", nombreInquilino='" + nombreInquilino + '\'' +
                '}';
    }
}

