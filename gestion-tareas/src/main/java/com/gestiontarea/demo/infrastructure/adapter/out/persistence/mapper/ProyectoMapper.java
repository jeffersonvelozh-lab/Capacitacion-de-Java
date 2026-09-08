package com.gestiontarea.demo.infrastructure.adapter.out.persistence.mapper;

import com.gestiontarea.demo.domain.models.Proyecto;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.ProyectoEntity;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.UsuarioEntity;

public class ProyectoMapper {

    public static Proyecto aDominio(ProyectoEntity entity) {
        return new Proyecto(
            entity.getId(),
            entity.getNombre(),
            entity.getDescripcion(),
            entity.getPropietario().getId()
        );
    }

    /**
     * @param propietarioRef referencia (proxy) a UsuarioEntity, resuelta por el
     *                        adapter via usuarioJpaRepository.getReferenceById(...)
     *                        para no traer el usuario completo solo por la FK.
     */
    public static ProyectoEntity aEntidad(Proyecto dominio, UsuarioEntity propietarioRef) {
        ProyectoEntity entity = new ProyectoEntity(
                dominio.getNombre(),
                dominio.getDescripcion(),
                propietarioRef
        );
        entity.setId(dominio.getId());
        return entity;
    }
}
