package com.gestiontarea.demo.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gestiontarea.demo.application.port.in.GestionarTareaUseCase;
import com.gestiontarea.demo.application.port.out.ProyectoRepositoryPort;
import com.gestiontarea.demo.application.port.out.TareaRepositoryPort;
import com.gestiontarea.demo.domain.models.EstadoTarea;
import com.gestiontarea.demo.domain.models.Tarea;
import com.gestiontarea.demo.exception.RecursoNoEncontradoException;

/**
 * TODO: implementa el CRUD de Tarea.
 * Idea para practicar logica de dominio real: en vez de simplemente hacer
 * "setEstado(nuevoEstado)", valida transiciones legales dentro del propio
 * modelo Tarea (ej: no se puede pasar de PENDIENTE a COMPLETADA sin pasar
 * por EN_PROGRESO) -> tarea.cambiarEstado(nuevoEstado) que lance una
 * excepcion de dominio si la transicion no es valida.
 */

@Service 
public class ServicioTarea implements GestionarTareaUseCase {

    private final TareaRepositoryPort tareaRepository;
    private final ProyectoRepositoryPort proyectoRepository;

    public ServicioTarea(TareaRepositoryPort tareaRepository, 
        ProyectoRepositoryPort proyectoRepository) {
        this.tareaRepository = tareaRepository;
        this.proyectoRepository = proyectoRepository;
    }

    @Override
    public Tarea crear(String titulo, String descripcion, Long proyectoId) {
        proyectoRepository.buscarPorId(proyectoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado: " + proyectoId));
        
        Tarea tarea = new Tarea(null, titulo, descripcion, EstadoTarea.PENDIENTE, proyectoId, null);
        return tareaRepository.guardar(tarea);
    }

    @Override
    public Tarea obtener(Long id) {
        return buscarOLanzar(id);
    }

    @Override
    public List<Tarea> listarPorProyecto(Long proyectoId) {
        return tareaRepository.listarPorProyecto(proyectoId);
    }

    @Override
    public Tarea asignar(Long tareaId, Long usuarioId) {
        Tarea tarea = buscarOLanzar(usuarioId);
        tarea.asignar(usuarioId);
        return tareaRepository.guardar(tarea);
    }

    @Override
    public Tarea cambiarEstado(Long tareaId, EstadoTarea nuevoEstado) {
        Tarea tarea = buscarOLanzar(tareaId);
        tarea.cambiarEstado(nuevoEstado);
        return tareaRepository.guardar(tarea);
    }

    @Override
    public void eliminar(Long id) {
        buscarOLanzar(id);
        tareaRepository.eliminar(id);
    }

    private Tarea buscarOLanzar(Long id) {
        return tareaRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tarea no encontrada: " + id));
    }
}
