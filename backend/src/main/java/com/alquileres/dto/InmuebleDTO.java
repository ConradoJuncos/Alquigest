package com.alquileres.dto;

import com.alquileres.model.Inmueble;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import jakarta.validation.constraints.Size;

public class InmuebleDTO {

    private Long id;

    @NotNull(message = "El propietario es obligatorio")
    private Long propietarioId;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 100, message = "La dirección no puede exceder 100 caracteres")
    private String direccion;

    private Integer tipoInmuebleId;

    @Size(max = 20, message = "El tipo no puede exceder 20 caracteres")
    private String tipo;

    @NotNull(message = "El estado es obligatorio")
    private Integer estado;

    @PositiveOrZero(message = "La superficie debe ser positiva")
    private BigDecimal superficie;

    private Boolean esAlquilado;

    private Boolean esActivo;

    // Constructor por defecto
    public InmuebleDTO() {
    }

    // Constructor desde entidad
    public InmuebleDTO(Inmueble inmueble) {
        this.id = inmueble.getId();
        this.propietarioId = inmueble.getPropietarioId();
        this.direccion = inmueble.getDireccion();
        this.tipoInmuebleId = inmueble.getTipoInmuebleId();
        this.tipo = inmueble.getTipo();
        this.estado = inmueble.getEstado();
        this.superficie = inmueble.getSuperficie();
        this.esAlquilado = inmueble.getEsAlquilado();
        this.esActivo = inmueble.getEsActivo();
    }

    // Método para convertir a entidad
    public Inmueble toEntity() {
        Inmueble inmueble = new Inmueble();
        inmueble.setId(this.id);
        inmueble.setPropietarioId(this.propietarioId);
        inmueble.setDireccion(this.direccion);
        inmueble.setTipoInmuebleId(this.tipoInmuebleId);
        inmueble.setTipo(this.tipo);
        inmueble.setEstado(this.estado);
        inmueble.setSuperficie(this.superficie);
        inmueble.setEsAlquilado(this.esAlquilado != null ? this.esAlquilado : false);
        inmueble.setEsActivo(this.esActivo != null ? this.esActivo : true);
        return inmueble;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPropietarioId() {
        return propietarioId;
    }

    public void setPropietarioId(Long propietarioId) {
        this.propietarioId = propietarioId;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getTipoInmuebleId() {
        return tipoInmuebleId;
    }

    public void setTipoInmuebleId(Integer tipoInmuebleId) {
        this.tipoInmuebleId = tipoInmuebleId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public BigDecimal getSuperficie() {
        return superficie;
    }

    public void setSuperficie(BigDecimal superficie) {
        this.superficie = superficie;
    }

    public Boolean getEsAlquilado() {
        return esAlquilado;
    }

    public void setEsAlquilado(Boolean esAlquilado) {
        this.esAlquilado = esAlquilado;
    }

    public Boolean getEsActivo() {
        return esActivo;
    }

    public void setEsActivo(Boolean esActivo) {
        this.esActivo = esActivo;
    }
}
