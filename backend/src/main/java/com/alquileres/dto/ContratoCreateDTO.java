package com.alquileres.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class ContratoCreateDTO {

    @NotNull(message = "El inmueble es obligatorio")
    private Long inmuebleId;

    @NotNull(message = "El inquilino es obligatorio")
    private Long inquilinoId;

    private String fechaInicio;

    private String fechaFin;

    @Positive(message = "El monto debe ser positivo")
    private BigDecimal monto;

    @PositiveOrZero(message = "El porcentaje de aumento debe ser positivo o cero")
    private BigDecimal porcentajeAumento;

    private Integer estadoContratoId;

    private Boolean aumentaConIcl;

    @Size(max = 500, message = "La ruta del PDF no puede exceder 500 caracteres")
    private String pdfPath;

    // Constructores
    public ContratoCreateDTO() {
    }

    public ContratoCreateDTO(Long inmuebleId, Long inquilinoId, String fechaInicio,
                            String fechaFin, BigDecimal monto, Integer estadoContratoId) {
        this.inmuebleId = inmuebleId;
        this.inquilinoId = inquilinoId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.monto = monto;
        this.estadoContratoId = estadoContratoId;
        this.aumentaConIcl = false;
    }

    // Método para convertir a ContratoDTO
    public ContratoDTO toContratoDTO() {
        ContratoDTO contratoDTO = new ContratoDTO();
        contratoDTO.setInmuebleId(this.inmuebleId);
        contratoDTO.setInquilinoId(this.inquilinoId);
        contratoDTO.setFechaInicio(this.fechaInicio);
        contratoDTO.setFechaFin(this.fechaFin);
        contratoDTO.setMonto(this.monto);
        contratoDTO.setPorcentajeAumento(this.porcentajeAumento);
        contratoDTO.setEstadoContratoId(this.estadoContratoId);
        contratoDTO.setAumentaConIcl(this.aumentaConIcl);
        contratoDTO.setPdfPath(this.pdfPath);
        return contratoDTO;
    }

    // Getters y Setters
    public Long getInmuebleId() {
        return inmuebleId;
    }

    public void setInmuebleId(Long inmuebleId) {
        this.inmuebleId = inmuebleId;
    }

    public Long getInquilinoId() {
        return inquilinoId;
    }

    public void setInquilinoId(Long inquilinoId) {
        this.inquilinoId = inquilinoId;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public BigDecimal getPorcentajeAumento() {
        return porcentajeAumento;
    }

    public void setPorcentajeAumento(BigDecimal porcentajeAumento) {
        this.porcentajeAumento = porcentajeAumento;
    }

    public Integer getEstadoContratoId() {
        return estadoContratoId;
    }

    public void setEstadoContratoId(Integer estadoContratoId) {
        this.estadoContratoId = estadoContratoId;
    }

    public Boolean getAumentaConIcl() {
        return aumentaConIcl;
    }

    public void setAumentaConIcl(Boolean aumentaConIcl) {
        this.aumentaConIcl = aumentaConIcl;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    @Override
    public String toString() {
        return "ContratoCreateDTO{" +
                "inmuebleId=" + inmuebleId +
                ", inquilinoId=" + inquilinoId +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", monto=" + monto +
                ", estadoContratoId=" + estadoContratoId +
                '}';
    }
}
