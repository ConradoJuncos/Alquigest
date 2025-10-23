package com.alquileres.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ambito_pdfs")
public class AmbitoPDF {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre; // "CONTRATO", "PAGO_SERVICIO", "PAGO_ALQUILER"

    public AmbitoPDF() {
    }

    public AmbitoPDF(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        return "AmbitoPDF{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}

