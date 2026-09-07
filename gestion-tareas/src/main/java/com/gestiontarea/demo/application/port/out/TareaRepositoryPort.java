package com.gestiontarea.demo.application.port.out;

import java.util.List;
import java.util.Optional;

import com.gestiontarea.demo.domain.models.Tarea;

public interface TareaRepositoryPort {
    Tarea guardar(Tarea tarea);

    Optional<Tarea> buscarPorId(Long id);

    List<Tarea> listarPorProyecto(Long proyectoId);

    List<Tarea> listarPorAsignado(Long usuarioId);

    void eliminar(Long id);
}
