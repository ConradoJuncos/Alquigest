package com.alquileres.dto;

/**
 * DTO para la respuesta de una cancelación de contrato
 */
public class CancelacionContratoDTO {

    private Long id;
    private Long contratoId;
    private String fechaCancelacion;
    private String motivoCancelacionNombre;
    private String observaciones;

    // Constructores
    public CancelacionContratoDTO() {
    }

    public CancelacionContratoDTO(Long id, Long contratoId, String fechaCancelacion,
                                  String motivoCancelacionNombre, String observaciones) {
        this.id = id;
        this.contratoId = contratoId;
        this.fechaCancelacion = fechaCancelacion;
        this.motivoCancelacionNombre = motivoCancelacionNombre;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public String getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void setFechaCancelacion(String fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    public String getMotivoCancelacionNombre() {
        return motivoCancelacionNombre;
    }

    public void setMotivoCancelacionNombre(String motivoCancelacionNombre) {
        this.motivoCancelacionNombre = motivoCancelacionNombre;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "CancelacionContratoDTO{" +
                "id=" + id +
                ", contratoId=" + contratoId +
                ", fechaCancelacion='" + fechaCancelacion + '\'' +
                ", motivoCancelacionNombre='" + motivoCancelacionNombre + '\'' +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}
