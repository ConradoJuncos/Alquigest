package com.alquileres.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
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

    private Boolean aumentaConIcl;


    @Min(value = 1, message = "El período de aumento debe ser mínimo 1 mes")
    @Max(value = 12, message = "El período de aumento debe ser máximo 12 meses")
    private Integer periodoAumento;

    // Campo interno para el estado del contrato (se asigna automáticamente, no se solicita al usuario)
    private Integer estadoContratoId;

    // Constructores
    public ContratoCreateDTO() {
    }

    public ContratoCreateDTO(Long inmuebleId, Long inquilinoId, String fechaInicio,
                            String fechaFin, BigDecimal monto) {
        this.inmuebleId = inmuebleId;
        this.inquilinoId = inquilinoId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.monto = monto;
        this.aumentaConIcl = true;
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
        contratoDTO.setAumentaConIcl(this.aumentaConIcl);
        contratoDTO.setPeriodoAumento(this.periodoAumento);
        // estadoContratoId y fechaAumento se calculan automáticamente en el servicio
        // El PDF se carga después a través del endpoint dedicado
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

    public Boolean getAumentaConIcl() {
        return aumentaConIcl;
    }

    public void setAumentaConIcl(Boolean aumentaConIcl) {
        this.aumentaConIcl = aumentaConIcl;
    }


    public Integer getPeriodoAumento() {
        return periodoAumento;
    }

    public void setPeriodoAumento(Integer periodoAumento) {
        this.periodoAumento = periodoAumento;
    }

    public Integer getEstadoContratoId() {
        return estadoContratoId;
    }

    public void setEstadoContratoId(Integer estadoContratoId) {
        this.estadoContratoId = estadoContratoId;
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
