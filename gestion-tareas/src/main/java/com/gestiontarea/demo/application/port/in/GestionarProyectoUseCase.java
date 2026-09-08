package com.gestiontarea.demo.application.port.in;

import java.util.List;

import com.gestiontarea.demo.domain.models.Proyecto;
import com.gestiontarea.demo.domain.models.Rol;

public interface GestionarProyectoUseCase {
    Proyecto crear(String nombre, String descripcion, Long propietarioId);
    Proyecto obtener(Long id, Long usuarioActualId, Rol rolUsuarioActual);
    List<Proyecto> listarPorPropietario(Long propietarioId, Long usuarioActualId, Rol rolUsuarioActual);
    Proyecto actualizar(Long id, String nombre, String descripcion, Long usuarioActualId, Rol rolUsuarioActual);
    void eliminar(Long id, Long usuarioActualId, Rol rolUsuarioActual);
}
