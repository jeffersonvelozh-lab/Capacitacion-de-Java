package com.gestiontarea.demo.application.service;

import java.util.List;

import com.gestiontarea.demo.application.port.in.GestionarTareaUseCase;
import com.gestiontarea.demo.application.port.out.TareaRepositoryPort;
import com.gestiontarea.demo.domain.models.EstadoTarea;
import com.gestiontarea.demo.domain.models.Tarea;

/**
 * TODO: implementa el CRUD de Tarea.
 * Idea para practicar logica de dominio real: en vez de simplemente hacer
 * "setEstado(nuevoEstado)", valida transiciones legales dentro del propio
 * modelo Tarea (ej: no se puede pasar de PENDIENTE a COMPLETADA sin pasar
 * por EN_PROGRESO) -> tarea.cambiarEstado(nuevoEstado) que lance una
 * excepcion de dominio si la transicion no es valida.
 */

public class ServicioTarea implements GestionarTareaUseCase {

    private final TareaRepositoryPort tareaRepository;

    public ServicioTarea(TareaRepositoryPort tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    @Override
    public Tarea crear(String titulo, String descripcion, Long proyectoId) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public Tarea obtener(Long id) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public List<Tarea> listarPorProyecto(Long proyectoId) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public Tarea asignar(Long tareaId, Long usuarioId) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public Tarea cambiarEstado(Long tareaId, EstadoTarea nuevoEstado) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public void eliminar(Long id) {
        throw new UnsupportedOperationException("TODO: implementar");
    }
}
