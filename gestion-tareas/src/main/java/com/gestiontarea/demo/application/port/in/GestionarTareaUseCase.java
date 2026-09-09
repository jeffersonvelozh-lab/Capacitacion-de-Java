package com.gestiontarea.demo.application.port.in;

import java.util.List;

import com.gestiontarea.demo.domain.models.EstadoTarea;
import com.gestiontarea.demo.domain.models.Rol;
import com.gestiontarea.demo.domain.models.Tarea;

public interface GestionarTareaUseCase {
    Tarea crear(String titulo, String descripcion, Long proyectoId, 
        Long usuarioActualId, Rol rolUsuarioActual);

    Tarea obtener(Long id);

    List<Tarea> listarPorProyecto(Long proyectoId);

    Tarea asignar(Long tareaId, Long usuarioId, 
        Long usuarioActualId, Rol rolUsuarioActual);

    Tarea cambiarEstado(Long tareaId, EstadoTarea nuevoEstado);

    void eliminar(Long id);
}
