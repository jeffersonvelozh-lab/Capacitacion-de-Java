package com.gestiontarea.demo.infrastructure.adapter.out.persistence.mapper;

import com.gestiontarea.demo.domain.models.Usuario;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.UsuarioEntity;

/**
 * Traduce entre el modelo de dominio (Usuario) y la entidad JPA (UsuarioEntity).
 * Esta clase es el "pegamento" que permite que domain/ nunca importe jakarta.persistence.
 *
 * implementa aTareaDominio / aEntidad usando los getters/setters/constructores
 * que definas en Usuario y UsuarioEntity.
 */
public class UsuarioMapper {

    public static Usuario aDominio(UsuarioEntity entity) {
        return new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRol(),
                entity.isActivo()
        );
    }

    public static UsuarioEntity aEntidad(Usuario dominio) {
         UsuarioEntity entity = new UsuarioEntity(
                dominio.getNombre(),
                dominio.getEmail(),
                dominio.getPasswordHash(),
                dominio.getRol(),
                dominio.isActivo()
        );
        entity.setId(dominio.getId());
        return entity;
    }
}
