package com.alquileres.model;

import jakarta.persistence.*;

@Entity
@Table(name = "estado_inmueble")
public class EstadoInmueble {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;

    public EstadoInmueble() {}

    public EstadoInmueble(String nombre) {
        this.nombre = nombre;
    }

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
}
