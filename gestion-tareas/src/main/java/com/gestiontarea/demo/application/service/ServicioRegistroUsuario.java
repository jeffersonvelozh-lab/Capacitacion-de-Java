package com.gestiontarea.demo.application.service;

import com.gestiontarea.demo.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Service;

import com.gestiontarea.demo.application.port.in.RegistrarUsuarioUseCase;
import com.gestiontarea.demo.application.port.out.PasswordHasherPort;
import com.gestiontarea.demo.application.port.out.UsuarioRepositoryPort;
import com.gestiontarea.demo.domain.models.Rol;
import com.gestiontarea.demo.domain.models.Usuario;
import com.gestiontarea.demo.exception.EmailYaRegistradoException;

@Service 
public class ServicioRegistroUsuario implements RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordHasherPort passwordHasher;

    public ServicioRegistroUsuario(UsuarioRepositoryPort usuarioRepository,
                                    PasswordHasherPort passwordHasher, UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public Usuario registrar(String nombre, String email, String passwordPlano) {
        if (usuarioRepository.existePorEmail(email)) {
            throw new EmailYaRegistradoException(email);
        }

        String passwordHash = passwordHasher.hashear(passwordPlano);

        Usuario nuevoUsuario = new Usuario(
            null, 
            nombre,
            email, 
            passwordHash,
            Rol.USER,
            true
        );

        return usuarioRepository.guardar(nuevoUsuario);
    }
}
