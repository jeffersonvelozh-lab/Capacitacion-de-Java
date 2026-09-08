package com.gestiontarea.demo.infrastructure.adapter.out.persistence.mapper;

import com.gestiontarea.demo.domain.models.Tarea;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.ProyectoEntity;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.TareaEntity;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.UsuarioEntity;

public class TareaMapper {

    public static Tarea aDominio(TareaEntity entity) {
        Long asignadoAId = entity.getAsignadoA() != null
                ? entity.getAsignadoA().getId()
                : null;

        return new Tarea(
                entity.getId(),
                entity.getTitulo(),
                entity.getDescripcion(),
                entity.getEstado(),
                entity.getProyecto().getId(),
                asignadoAId
        );
    }

     /**
     * @param proyectoRef  referencia (proxy) a ProyectoEntity, resuelta por el
     *                      adapter via proyectoJpaRepository.getReferenceById(...)
     * @param asignadoARef referencia a UsuarioEntity si la tarea tiene asignado,
     *                      o null si no tiene (asignadoAId era null en el dominio)
     */

    public static TareaEntity aEntidad(Tarea dominio, ProyectoEntity proyectoRef, UsuarioEntity asignadoARef) {
        TareaEntity entity = new TareaEntity(
            dominio.getTitulo(),
            dominio.getDescripcion(),
            dominio.getEstado(),
            proyectoRef,
            asignadoARef
        );

        entity.setId(dominio.getId());
        return entity;
    }
}
