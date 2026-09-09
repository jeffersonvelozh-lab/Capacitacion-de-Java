package com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity;

import com.gestiontarea.demo.domain.models.EstadoTarea;

import jakarta.persistence.*;

/**
 * completa los campos (titulo, descripcion, estado, proyecto, asignadoA).
 * Estado se guarda como String via @Enumerated(EnumType.STRING) -- NUNCA
 * uses EnumType.ORDINAL (se rompe si reordenas el enum).
 */
@Entity
@Table(name = "tareas")
public class TareaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTarea estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private ProyectoEntity proyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignado_a_id")
    private UsuarioEntity asignadoA;

    protected TareaEntity(){}

    public TareaEntity(String titulo, String descripcion, EstadoTarea estado, 
        ProyectoEntity proyecto, UsuarioEntity asignadoA) {

        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.proyecto = proyecto;
        this.asignadoA = asignadoA;
    
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public EstadoTarea getEstado() { return estado; }
    public void setEstado(EstadoTarea estado) { this.estado = estado; }

    public ProyectoEntity getProyecto() { return proyecto; }
    public void setProyecto(ProyectoEntity proyecto) { this.proyecto = proyecto; }

    public UsuarioEntity getAsignadoA() { return asignadoA; }
    public void setAsignadoA(UsuarioEntity asignadoA) { this.asignadoA = asignadoA; }
}
