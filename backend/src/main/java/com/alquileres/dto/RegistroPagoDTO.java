package com.alquileres.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegistroPagoDTO {

    @NotNull(message = "La cuenta de banco es obligatoria")
    @Size(max = 100, message = "La cuenta de banco no puede exceder 100 caracteres")
    private String cuentaBanco;

    @NotNull(message = "El titular de pago es obligatorio")
    @Size(max = 100, message = "El titular de pago no puede exceder 100 caracteres")
    private String titularDePago;

    @NotNull(message = "El método de pago es obligatorio")
    @Size(max = 50, message = "El método no puede exceder 50 caracteres")
    private String metodo;

    // Constructor vacío
    public RegistroPagoDTO() {
    }

    // Constructor con parámetros
    public RegistroPagoDTO(String cuentaBanco, String titularDePago, String metodo) {
        this.cuentaBanco = cuentaBanco;
        this.titularDePago = titularDePago;
        this.metodo = metodo;
    }

    // Getters y Setters
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
}

