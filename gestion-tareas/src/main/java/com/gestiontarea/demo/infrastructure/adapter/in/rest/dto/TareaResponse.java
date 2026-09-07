package com.gestiontarea.demo.infrastructure.adapter.in.rest.dto;

import com.gestiontarea.demo.domain.models.Tarea;

public record TareaResponse(Long id, String titulo, String descripcion, String estado,
    Long proyectoId, Long asignadoAId) {

        public static TareaResponse from(Tarea tarea) {
            return new TareaResponse(
                tarea.getId(), 
                tarea.getTitulo(), 
                tarea.getDescripcion(), 
                tarea.getEstado().name(), 
                tarea.getProyectoId(), 
                tarea.getAsignadoAId()
            );
        }
}
