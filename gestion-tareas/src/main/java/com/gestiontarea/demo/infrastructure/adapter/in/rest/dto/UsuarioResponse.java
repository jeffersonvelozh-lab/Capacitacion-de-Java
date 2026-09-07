package com.gestiontarea.demo.infrastructure.adapter.in.rest.dto;

import com.gestiontarea.demo.domain.models.Usuario;

public record UsuarioResponse(Long id, String nombre, String email, String rol) {
    
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(), 
            usuario.getNombre(), 
            usuario.getEmail(),
            usuario.getRol().name()
        );
    }
}
