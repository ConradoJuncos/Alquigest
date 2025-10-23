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

    private Integer estadoContratoId;

    private Boolean aumentaConIcl;

    @PositiveOrZero(message = "El período de aumento debe ser positivo o cero")
    private Integer periodoAumento;

    private String fechaAumento;

    // Campos adicionales para mostrar información relacionada (solo lectura)
    private String direccionInmueble;
    private String tipoInmueble;
    private BigDecimal superficieInmueble;

    private String nombreInquilino;
    private String apellidoInquilino;
    private String cuilInquilino;
    private String telefonoInquilino;

    private String nombrePropietario;
    private String apellidoPropietario;
    private String dniPropietario;
    private String telefonoPropietario;
    private String emailPropietario;
    private String direccionPropietario;
    private String claveFiscalPropietario;

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
        this.estadoContratoId = contrato.getEstadoContrato() != null ? contrato.getEstadoContrato().getId().intValue() : null;
        this.aumentaConIcl = contrato.getAumentaConIcl();
        this.periodoAumento = contrato.getPeriodoAumento();
        this.fechaAumento = contrato.getFechaAumento();

        // Información adicional para mostrar
        this.direccionInmueble = contrato.getInmueble() != null ? contrato.getInmueble().getDireccion() : null;
        // El nombre del tipo de inmueble se completará en el servicio
        this.tipoInmueble = null;
        this.superficieInmueble = contrato.getInmueble() != null ? contrato.getInmueble().getSuperficie() : null;

        this.nombreInquilino = contrato.getInquilino() != null ? contrato.getInquilino().getNombre() : null;
        this.apellidoInquilino = contrato.getInquilino() != null ? contrato.getInquilino().getApellido() : null;
        this.cuilInquilino = contrato.getInquilino() != null ? contrato.getInquilino().getCuil() : null;
        this.telefonoInquilino = contrato.getInquilino() != null ? contrato.getInquilino().getTelefono() : null;

        this.estadoContratoNombre = contrato.getEstadoContrato() != null ? contrato.getEstadoContrato().getNombre() : null;

        // Agregar información del propietario a través del inmueble
        if (contrato.getInmueble() != null && contrato.getInmueble().getPropietarioId() != null) {
            // Nota: Para obtener la información completa del propietario necesitamos hacer una consulta adicional
            // Por ahora dejamos estos campos como null, se completarán en el servicio
            this.nombrePropietario = null;
            this.apellidoPropietario = null;
            this.dniPropietario = null;
            this.telefonoPropietario = null;
            this.emailPropietario = null;
            this.direccionPropietario = null;
            this.claveFiscalPropietario = null;
        }
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

    public Integer getPeriodoAumento() {
        return periodoAumento;
    }

    public void setPeriodoAumento(Integer periodoAumento) {
        this.periodoAumento = periodoAumento;
    }

    public String getFechaAumento() {
        return fechaAumento;
    }

    public void setFechaAumento(String fechaAumento) {
        this.fechaAumento = fechaAumento;
    }

    public String getDireccionInmueble() {
        return direccionInmueble;
    }

    public void setDireccionInmueble(String direccionInmueble) {
        this.direccionInmueble = direccionInmueble;
    }

    public String getTipoInmueble() {
        return tipoInmueble;
    }

    public void setTipoInmueble(String tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public BigDecimal getSuperficieInmueble() {
        return superficieInmueble;
    }

    public void setSuperficieInmueble(BigDecimal superficieInmueble) {
        this.superficieInmueble = superficieInmueble;
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

    public String getCuilInquilino() {
        return cuilInquilino;
    }

    public void setCuilInquilino(String cuilInquilino) {
        this.cuilInquilino = cuilInquilino;
    }

    public String getTelefonoInquilino() {
        return telefonoInquilino;
    }

    public void setTelefonoInquilino(String telefonoInquilino) {
        this.telefonoInquilino = telefonoInquilino;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public String getApellidoPropietario() {
        return apellidoPropietario;
    }

    public void setApellidoPropietario(String apellidoPropietario) {
        this.apellidoPropietario = apellidoPropietario;
    }

    public String getDniPropietario() {
        return dniPropietario;
    }

    public void setDniPropietario(String dniPropietario) {
        this.dniPropietario = dniPropietario;
    }

    public String getTelefonoPropietario() {
        return telefonoPropietario;
    }

    public void setTelefonoPropietario(String telefonoPropietario) {
        this.telefonoPropietario = telefonoPropietario;
    }

    public String getEmailPropietario() {
        return emailPropietario;
    }

    public void setEmailPropietario(String emailPropietario) {
        this.emailPropietario = emailPropietario;
    }

    public String getDireccionPropietario() {
        return direccionPropietario;
    }

    public void setDireccionPropietario(String direccionPropietario) {
        this.direccionPropietario = direccionPropietario;
    }

    public String getClaveFiscalPropietario() {
        return claveFiscalPropietario;
    }

    public void setClaveFiscalPropietario(String claveFiscalPropietario) {
        this.claveFiscalPropietario = claveFiscalPropietario;
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
