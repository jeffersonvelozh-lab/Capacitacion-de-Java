package com.gestiontarea.demo.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.gestiontarea.demo.application.port.out.TareaRepositoryPort;
import com.gestiontarea.demo.domain.models.Tarea;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.ProyectoEntity;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.TareaEntity;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.mapper.TareaMapper;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.repository.ProyectoJpaRepository;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.repository.TareaJpaRepository;
import com.gestiontarea.demo.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * implementa siguiendo el patron de UsuarioRepositoryAdapter.
 */
@Component
public class TareaRepositoryAdapter implements TareaRepositoryPort {

    private final TareaJpaRepository jpaRepository;
    private final ProyectoJpaRepository proyectoJpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;

    public TareaRepositoryAdapter(TareaJpaRepository jpaRepository, ProyectoJpaRepository proyectoJpaRepository,
                                   UsuarioJpaRepository usuarioJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.proyectoJpaRepository = proyectoJpaRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public Tarea guardar(Tarea tarea) {
        ProyectoEntity proyectoRef = proyectoJpaRepository.getReferenceById(tarea.getProyectoId());

        UsuarioEntity asignadoARef = tarea.getAsignadoAId() != null
                ? usuarioJpaRepository.getReferenceById(tarea.getAsignadoAId())
                : null;

        TareaEntity entity = TareaMapper.aEntidad(tarea, proyectoRef, asignadoARef);
        TareaEntity guardado = jpaRepository.save(entity);
        return TareaMapper.aDominio(guardado);
    }

    @Override
    public Optional<Tarea> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(TareaMapper::aDominio);
    }

    @Override
    public List<Tarea> listarPorProyecto(Long proyectoId) {
        return jpaRepository.findByProyectoId(proyectoId)
                .stream()
                .map(TareaMapper::aDominio)
                .toList();
    }

    @Override
    public List<Tarea> listarPorAsignado(Long usuarioId) {
        return jpaRepository.findByAsignadoAId(usuarioId)
                .stream()
                .map(TareaMapper::aDominio)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
