package com.alquileres.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "estado_contrato")
public class EstadoContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 50, unique = true)
    private String nombre;

    // Constructor por defecto
    public EstadoContrato() {
    }

    // Constructor con parámetros
    public EstadoContrato(String nombre) {
        this.nombre = nombre;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "EstadoContrato{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
