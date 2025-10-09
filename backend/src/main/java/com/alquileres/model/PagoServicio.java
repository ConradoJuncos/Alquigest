package com.alquileres.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "pago_servicio")
public class PagoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull(message = "El servicio x inmueble es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_x_inmueble_id", nullable = false)
    private ServicioXInmueble servicioXInmueble;

    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{4}$", message = "El periodo debe tener el formato mm/aaaa (ej: 01/2025)")
    @Column(name = "periodo", length = 7)
    private String periodo; // Formato: mm/aaaa (ej: 01/2025) - representa el mes/año de la factura

    @Column(name = "fecha_pago")
    private String fechaPago;

    @Column(name = "fecha_vencimiento")
    private String fechaVencimiento;

    @Column(name = "esta_pagado", nullable = false)
    private Boolean estaPagado = false;

    @Column(name = "esta_vencido", nullable = false)
    private Boolean estaVencido = false;

    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    @Column(name = "medio_pago", length = 50)
    private String medioPago;

    @Column(name = "titular", length = 100)
    private String titular;

    @Column(name = "monto", precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    // Constructor por defecto
    public PagoServicio() {
    }

    // Constructor con parámetros principales
    public PagoServicio(ServicioXInmueble servicioXInmueble, String periodo, String fechaVencimiento, BigDecimal monto) {
        this.servicioXInmueble = servicioXInmueble;
        this.periodo = periodo;
        this.fechaVencimiento = fechaVencimiento;
        this.monto = monto;
        this.estaPagado = false;
        this.estaVencido = false;
    }

    @PrePersist
    protected void onCreate() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ServicioXInmueble getServicioXInmueble() {
        return servicioXInmueble;
    }

    public void setServicioXInmueble(ServicioXInmueble servicioXInmueble) {
        this.servicioXInmueble = servicioXInmueble;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Boolean getEstaPagado() {
        return estaPagado;
    }

    public void setEstaPagado(Boolean estaPagado) {
        this.estaPagado = estaPagado;
    }

    public Boolean getEstaVencido() {
        return estaVencido;
    }

    public void setEstaVencido(Boolean estaVencido) {
        this.estaVencido = estaVencido;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
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

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    @Override
    public String toString() {
        return "PagoServicio{" +
                "id=" + id +
                ", periodo='" + periodo + '\'' +
                ", fechaPago='" + fechaPago + '\'' +
                ", fechaVencimiento='" + fechaVencimiento + '\'' +
                ", estaPagado=" + estaPagado +
                ", estaVencido=" + estaVencido +
                ", medioPago='" + medioPago + '\'' +
                ", titular='" + titular + '\'' +
                ", monto=" + monto +
                '}';
    }
}
