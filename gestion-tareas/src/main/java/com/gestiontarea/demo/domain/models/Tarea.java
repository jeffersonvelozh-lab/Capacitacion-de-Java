package com.gestiontarea.demo.domain.models;

/**
 *   agrega comportamiento de dominio, ej:
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

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }


    // Inicia la tarea: PENDIENTE -> EN_PROGRESO.
    
    public void iniciar() {
        if (estado != EstadoTarea.PENDIENTE) {
            throw new IllegalStateException(
                "Solo una tarea PENDIENTE puede iniciarse. Estado actual: " + estado);
        }
        this.estado = EstadoTarea.EN_PROGRESO;
    }

    // Completa la tarea: EN_PROGRESO -> COMPLETADA. 
    public void completar() {
        if (estado != EstadoTarea.EN_PROGRESO) {
            throw new IllegalStateException(
                "Solo una tarea EN_PROGRESO puede completarse. Estado actual: " +estado);
        }
        this.estado = EstadoTarea.COMPLETADA;
    }

    // Reabre una tarea completada: COMPLETADA -> EN_PROGRESO.
    public void reabrir() {
        if (estado != EstadoTarea.COMPLETADA) {
            throw new IllegalStateException(
                "Solo una tarea COMPLETADA puede reabririse. Estado actual: " +estado);
        }
        this.estado = EstadoTarea.EN_PROGRESO;
    }

    //Asigan la tarea a un usuario. No se puede asignar una tarea ya completada
    public void asignar(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException(
                "El id del usuario asignado no puede ser null"
            );
        }
        if (estado == EstadoTarea.COMPLETADA) {
            throw new IllegalStateException(
                "No se puede asignar una tarea que ya este completada"
            );
        }
        this.asignadoAId = usuarioId;
    }

    //Quita la asignacion actual de la tarea
    public void desasignar() {
        this.asignadoAId = null;
    }

    /**
     * Orquesta la transición hacia el estado destino, delegando en el método
     * de dominio correspondiente para que las reglas de negocio se validen
     * en un solo lugar.
     */
    public void cambiarEstado(EstadoTarea nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser null");
        }

        if (nuevoEstado == this.estado) {
            throw new IllegalStateException("La tarea ya se encuentra en estado " + nuevoEstado);
        }

        switch (nuevoEstado) {
            case EN_PROGRESO -> {
                if (this.estado == EstadoTarea.PENDIENTE) {
                    iniciar();
                } else if (this.estado == EstadoTarea.COMPLETADA) {
                    reabrir();
                }
            }
            case COMPLETADA -> completar();
            case PENDIENTE -> throw new IllegalStateException(
                    "No se puede volver una tarea a PENDIENTE una vez iniciada");
        }
    }

}
