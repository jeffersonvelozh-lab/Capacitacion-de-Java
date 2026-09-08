package com.gestiontarea.demo.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.gestiontarea.demo.application.port.out.UsuarioRepositoryPort;
import com.gestiontarea.demo.domain.models.Usuario;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.mapper.UsuarioMapper;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * EJEMPLO COMPLETO: asi se ve un adapter de salida.
 * Implementa el puerto de dominio (UsuarioRepositoryPort) usando Spring Data JPA
 * por debajo. Solo esta clase sabe que existe JPA/Hibernate -- el resto del
 * dominio y de application/ no tiene ni idea.
 */
@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = UsuarioMapper.aEntidad(usuario);
        UsuarioEntity guardado = jpaRepository.save(entity);
        return UsuarioMapper.aDominio(guardado);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(UsuarioMapper::aDominio);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email).map(UsuarioMapper::aDominio);
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public List<Usuario> listarTodos() {
        return jpaRepository.findAll().stream()
            .map(UsuarioMapper::aDominio)
            .toList();
    }
}
