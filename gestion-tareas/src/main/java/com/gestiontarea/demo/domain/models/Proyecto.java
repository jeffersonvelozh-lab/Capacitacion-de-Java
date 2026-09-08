package com.gestiontarea.demo.domain.models;

/**
 * define los campos (id, nombre, descripcion, propietario, tareas?)
 * Pregunta de diseno: ¿Proyecto conoce su lista de Tareas, o Tarea conoce su Proyecto (unidireccional)?
 * Para un CRUD simple, unidireccional (Tarea -> Proyecto) suele bastar y evita ciclos.
 */

public class Proyecto {
    private final Long id;
    private String nombre;
    private String descripcion;
    private Long propietarioId;

    public Proyecto(Long id, String nombre, String descripcion, Long propietarioId) {
        if (nombre == null || nombre.isBlank()) {
           throw new IllegalArgumentException("El nombre del proyecto no puede estar vacío");
       }
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.propietarioId = propietarioId;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Long getPropietarioId() { return propietarioId; }

    public void setNombre(String nombre){ this.nombre = nombre; }
    public void setDescripcion(String descripcion){ this.descripcion = descripcion; }
    public void setPropietarioId(Long propietarioId){ this.propietarioId = propietarioId; }
}
