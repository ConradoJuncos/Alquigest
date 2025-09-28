package com.alquileres.dto;

import com.alquileres.model.Contrato;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class ContratoDTO {

    private Long id;

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

    @NotNull(message = "El estado es obligatorio")
    private Integer estadoContratoId;

    private Boolean aumentaConIcl;

    @Size(max = 500, message = "La ruta del PDF no puede exceder 500 caracteres")
    private String pdfPath;

    // Campos adicionales para mostrar información relacionada (solo lectura)
    private String direccionInmueble;
    private String nombreInquilino;
    private String apellidoInquilino;
    private String estadoContratoNombre;

    // Constructor por defecto
    public ContratoDTO() {
    }

    // Constructor desde entidad (para respuestas)
    public ContratoDTO(Contrato contrato) {
        this.id = contrato.getId();
        this.inmuebleId = contrato.getInmueble() != null ? contrato.getInmueble().getId() : null;
        this.inquilinoId = contrato.getInquilino() != null ? contrato.getInquilino().getId() : null;
        this.fechaInicio = contrato.getFechaInicio();
        this.fechaFin = contrato.getFechaFin();
        this.monto = contrato.getMonto();
        this.porcentajeAumento = contrato.getPorcentajeAumento();
        this.estadoContratoId = contrato.getEstadoContrato() != null ? contrato.getEstadoContrato().getId() : null;
        this.aumentaConIcl = contrato.getAumentaConIcl();
        this.pdfPath = contrato.getPdfPath();

        // Información adicional para mostrar
        this.direccionInmueble = contrato.getInmueble() != null ? contrato.getInmueble().getDireccion() : null;
        this.nombreInquilino = contrato.getInquilino() != null ? contrato.getInquilino().getNombre() : null;
        this.apellidoInquilino = contrato.getInquilino() != null ? contrato.getInquilino().getApellido() : null;
        this.estadoContratoNombre = contrato.getEstadoContrato() != null ? contrato.getEstadoContrato().getNombre() : null;
    }

    // Constructor con parámetros principales
    public ContratoDTO(Long inmuebleId, Long inquilinoId, String fechaInicio,
                      String fechaFin, BigDecimal monto, Integer estadoContratoId) {
        this.inmuebleId = inmuebleId;
        this.inquilinoId = inquilinoId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.monto = monto;
        this.estadoContratoId = estadoContratoId;
        this.aumentaConIcl = false;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getDireccionInmueble() {
        return direccionInmueble;
    }

    public void setDireccionInmueble(String direccionInmueble) {
        this.direccionInmueble = direccionInmueble;
    }

    public String getNombreInquilino() {
        return nombreInquilino;
    }

    public void setNombreInquilino(String nombreInquilino) {
        this.nombreInquilino = nombreInquilino;
    }

    public String getApellidoInquilino() {
        return apellidoInquilino;
    }

    public void setApellidoInquilino(String apellidoInquilino) {
        this.apellidoInquilino = apellidoInquilino;
    }

    public String getEstadoContratoNombre() {
        return estadoContratoNombre;
    }

    public void setEstadoContratoNombre(String estadoContratoNombre) {
        this.estadoContratoNombre = estadoContratoNombre;
    }

    @Override
    public String toString() {
        return "ContratoDTO{" +
                "id=" + id +
                ", inmuebleId=" + inmuebleId +
                ", inquilinoId=" + inquilinoId +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", monto=" + monto +
                ", estadoContratoId=" + estadoContratoId +
                '}';
    }
}
