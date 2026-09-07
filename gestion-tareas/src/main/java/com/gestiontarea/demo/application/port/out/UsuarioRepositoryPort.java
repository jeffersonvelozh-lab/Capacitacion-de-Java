package com.gestiontarea.demo.application.port.out;

import java.util.Optional;

import com.gestiontarea.demo.domain.models.Usuario;

public interface UsuarioRepositoryPort {
    
    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    boolean existePorEmail(String email);

    // TODO: agrega los metodos que necesites (listarTodos, eliminar, etc.)
}
