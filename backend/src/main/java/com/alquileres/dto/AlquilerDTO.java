package com.alquileres.dto;

import com.alquileres.model.Alquiler;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class AlquilerDTO {

    private Long id;

    @NotNull(message = "El contrato es obligatorio")
    private Long contratoId;

    private String fechaVencimientoPago;

    @Positive(message = "El monto debe ser positivo")
    private BigDecimal monto;

    private Boolean estaPagado;

    @Size(max = 100, message = "La cuenta de banco no puede exceder 100 caracteres")
    private String cuentaBanco;

    @Size(max = 100, message = "El titular de pago no puede exceder 100 caracteres")
    private String titularDePago;

    @Size(max = 50, message = "El método no puede exceder 50 caracteres")
    private String metodo;

    private String createdAt;

    private String updatedAt;

    // Información adicional del contrato (para enriquecer la respuesta)
    private Long inmuebleId;
    private String direccionInmueble;
    private Long inquilinoId;
    private String nombreInquilino;
    private String apellidoInquilino;

    // Constructor vacío
    public AlquilerDTO() {
    }

    // Constructor desde entidad
    public AlquilerDTO(Alquiler alquiler) {
        this.id = alquiler.getId();
        this.contratoId = alquiler.getContrato() != null ? alquiler.getContrato().getId() : null;
        this.fechaVencimientoPago = alquiler.getFechaVencimientoPago();
        this.monto = alquiler.getMonto();
        this.estaPagado = alquiler.getEstaPagado();
        this.cuentaBanco = alquiler.getCuentaBanco();
        this.titularDePago = alquiler.getTitularDePago();
        this.metodo = alquiler.getMetodo();
        this.createdAt = alquiler.getCreatedAt();
        this.updatedAt = alquiler.getUpdatedAt();

        // Información del contrato
        if (alquiler.getContrato() != null) {
            if (alquiler.getContrato().getInmueble() != null) {
                this.inmuebleId = alquiler.getContrato().getInmueble().getId();
                this.direccionInmueble = alquiler.getContrato().getInmueble().getDireccion();
            }
            if (alquiler.getContrato().getInquilino() != null) {
                this.inquilinoId = alquiler.getContrato().getInquilino().getId();
                this.nombreInquilino = alquiler.getContrato().getInquilino().getNombre();
                this.apellidoInquilino = alquiler.getContrato().getInquilino().getApellido();
            }
        }
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

    public String getFechaVencimientoPago() {
        return fechaVencimientoPago;
    }

    public void setFechaVencimientoPago(String fechaVencimientoPago) {
        this.fechaVencimientoPago = fechaVencimientoPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Boolean getEstaPagado() {
        return estaPagado;
    }

    public void setEstaPagado(Boolean estaPagado) {
        this.estaPagado = estaPagado;
    }

    public String getCuentaBanco() {
        return cuentaBanco;
    }

    public void setCuentaBanco(String cuentaBanco) {
        this.cuentaBanco = cuentaBanco;
    }

    public String getTitularDePago() {
        return titularDePago;
    }

    public void setTitularDePago(String titularDePago) {
        this.titularDePago = titularDePago;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getInmuebleId() {
        return inmuebleId;
    }

    public void setInmuebleId(Long inmuebleId) {
        this.inmuebleId = inmuebleId;
    }

    public String getDireccionInmueble() {
        return direccionInmueble;
    }

    public void setDireccionInmueble(String direccionInmueble) {
        this.direccionInmueble = direccionInmueble;
    }

    public Long getInquilinoId() {
        return inquilinoId;
    }

    public void setInquilinoId(Long inquilinoId) {
        this.inquilinoId = inquilinoId;
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
}

