package com.gestiontarea.demo.domain.models;

/**
 * TODO: agrega comportamiento de dominio, ej:
 *   tarea.completar()          -> valida transicion de estado valida
 *   tarea.asignarA(usuarioId)  -> valida reglas de asignacion
 * Esa logica NO debe vivir en el Service ni en el Controller: es responsabilidad
 * del propio modelo de dominio (evita el "anemic domain model").
 */

public class Tarea {
    private final Long id;
    private String titulo;
    private String descripcion;
    private EstadoTarea estado;
    private final Long proyectoId;
    private Long asignadoAId;

    public Tarea(Long id, String titulo, String descripcion, EstadoTarea estado,
                 Long proyectoId, Long asignadoAId) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.proyectoId = proyectoId;
        this.asignadoAId = asignadoAId;
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public EstadoTarea getEstado() { return estado; }
    public Long getProyectoId() { return proyectoId; }
    public Long getAsignadoAId() { return asignadoAId; }
}
