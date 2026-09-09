package com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

/**
 * Sugerencia: usa @ManyToOne hacia UsuarioEntity para "propietario",
 * con fetch = FetchType.LAZY (regla de oro en JPA: LAZY por defecto).
 */
@Entity
@Table(name = "proyectos")
public class ProyectoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private UsuarioEntity propietario;

    protected  ProyectoEntity(){}

    public ProyectoEntity( String nombre, String descripcion, UsuarioEntity propietario) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.propietario = propietario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public UsuarioEntity getPropietario() { return propietario; }
    public void setPropietario(UsuarioEntity propietario) { this.propietario = propietario; }
}
