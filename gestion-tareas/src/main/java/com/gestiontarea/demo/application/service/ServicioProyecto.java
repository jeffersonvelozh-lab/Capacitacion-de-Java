package com.gestiontarea.demo.application.service;

import com.gestiontarea.demo.application.port.out.ProyectoRepositoryPort;
import com.gestiontarea.demo.application.port.in.GestionarProyectoUseCase;
import com.gestiontarea.demo.domain.models.Proyecto;

import java.util.List;

/**
 * TODO: implementa el CRUD de Proyecto.
 * Recuerda: la validacion de "el usuario autenticado es el propietario o es ADMIN"
 * puede vivir aqui (regla de negocio) en vez de en el Controller.
 */

public class ServicioProyecto implements GestionarProyectoUseCase {

    private final ProyectoRepositoryPort proyectoRepository;

    public ServicioProyecto(ProyectoRepositoryPort proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    public Proyecto crear(String nombre, String descripcion, Long propietarioId) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public Proyecto obtener(Long id) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public List<Proyecto> listarPorPropietario(Long propietarioId) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public Proyecto actualizar(Long id, String nombre, String descripcion) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override 
    public void eliminar(Long id) {
        throw new UnsupportedOperationException("TODO: implementar");
    }
}
