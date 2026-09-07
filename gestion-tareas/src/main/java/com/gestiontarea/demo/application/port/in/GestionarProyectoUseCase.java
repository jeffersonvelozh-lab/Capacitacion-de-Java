package com.gestiontarea.demo.application.port.in;

import java.util.List;

import com.gestiontarea.demo.domain.models.Proyecto;

public interface GestionarProyectoUseCase {
    Proyecto crear(String nombre, String descripcion, Long propietarioId);
    Proyecto obtener(Long id);
    List<Proyecto> listarPorPropietario(Long propietarioId);
    Proyecto actualizar(Long id, String nombre, String descripcion);
    void eliminar(Long id);
}
