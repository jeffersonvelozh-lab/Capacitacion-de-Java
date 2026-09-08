package com.gestiontarea.demo.infrastructure.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.gestiontarea.demo.application.port.out.ProyectoRepositoryPort;
import com.gestiontarea.demo.domain.models.Proyecto;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.ProyectoEntity;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.mapper.ProyectoMapper;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.repository.ProyectoJpaRepository;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * implementa siguiendo el patron de UsuarioRepositoryAdapter
 * (usa ProyectoMapper.aDominio / aEntidad).
 */
@Component
public class ProyectoRepositoryAdapter implements ProyectoRepositoryPort {

    private final ProyectoJpaRepository jpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;

    public ProyectoRepositoryAdapter(ProyectoJpaRepository jpaRepository,
                                      UsuarioJpaRepository usuarioJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public Proyecto guardar(Proyecto proyecto) {
        UsuarioEntity propietarioRef = usuarioJpaRepository.getReferenceById(proyecto.getPropietarioId());
        ProyectoEntity entity = ProyectoMapper.aEntidad(proyecto, propietarioRef);
        ProyectoEntity guardado = jpaRepository.save(entity);
        return ProyectoMapper.aDominio(guardado);
    }

    @Override
    public Optional<Proyecto> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(ProyectoMapper::aDominio);
    }

    @Override
    public List<Proyecto> listarPorPropietario(Long propietarioId) {
        return jpaRepository.findByPropietarioId(propietarioId)
                .stream()
                .map(ProyectoMapper::aDominio)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Proyecto> listarTodos(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(ProyectoMapper::aDominio);
    }
}
