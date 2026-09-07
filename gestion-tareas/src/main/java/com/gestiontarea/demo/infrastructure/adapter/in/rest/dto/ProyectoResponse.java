package com.gestiontarea.demo.infrastructure.adapter.in.rest.dto;

import com.gestiontarea.demo.domain.models.Proyecto;

public record ProyectoResponse(Long id, String nombre, String descripcion, Long propietarioId) {
    
    public static ProyectoResponse from(Proyecto proyecto){
        return new ProyectoResponse(
            proyecto.getId(),
            proyecto.getNombre(),
            proyecto.getDescripcion(),
            proyecto.getPropietarioId()
        );
    }
}
