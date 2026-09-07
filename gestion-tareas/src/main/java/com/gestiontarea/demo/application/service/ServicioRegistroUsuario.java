package com.gestiontarea.demo.application.service;

import com.gestiontarea.demo.application.port.in.RegistrarUsuarioUseCase;
import com.gestiontarea.demo.application.port.out.PasswordHasherPort;
import com.gestiontarea.demo.application.port.out.UsuarioRepositoryPort;
import com.gestiontarea.demo.domain.models.Usuario;

/**
 * TODO: implementa siguiendo el patron de ServicioAutenticacion.
 * Pasos sugeridos:
 *   1. Verificar que el email no exista ya (usuarioRepository.existePorEmail)
 *      -> si existe, lanzar una excepcion de dominio (EmailYaRegistradoException)
 *   2. Hashear el password con passwordHasher.hashear(...)
 *   3. Construir el Usuario de dominio (rol USER por defecto, activo=true)
 *   4. Guardarlo con usuarioRepository.guardar(...)
 */

public class ServicioRegistroUsuario implements RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordHasherPort passwordHasher;

    public ServicioRegistroUsuario(UsuarioRepositoryPort usuarioRepository,
                                    PasswordHasherPort passwordHasher) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public Usuario registrar(String nombre, String email, String passwordPlano) {
        throw new UnsupportedOperationException("TODO: implementar");
    }
}
