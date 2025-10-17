package com.alquileres.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

/**
 * DTO para actualizar un pago de servicio
 */
public class ActualizarPagoServicioRequest {

    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{4}$", message = "El periodo debe tener el formato mm/aaaa (ej: 01/2025)")
    private String periodo;

    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "La fecha de pago debe tener el formato dd/MM/yyyy")
    private String fechaPago;

    private Boolean estaPagado;

    private Boolean estaVencido;

    private String pdfPath;

    private String medioPago;

    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor que cero")
    private BigDecimal monto;

    // Constructor por defecto
    public ActualizarPagoServicioRequest() {
    }

    // Getters y Setters
    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
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

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    @Override
    public String toString() {
        return "ActualizarPagoServicioRequest{" +
                "periodo='" + periodo + '\'' +
                ", fechaPago='" + fechaPago + '\'' +
                ", estaPagado=" + estaPagado +
                ", estaVencido=" + estaVencido +
                ", pdfPath='" + pdfPath + '\'' +
                ", medioPago='" + medioPago + '\'' +
                ", monto=" + monto +
                '}';
    }
}

