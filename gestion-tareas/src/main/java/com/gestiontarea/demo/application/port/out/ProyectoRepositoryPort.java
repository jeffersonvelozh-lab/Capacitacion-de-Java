package com.gestiontarea.demo.application.port.out;

import java.util.List;
import java.util.Optional;

import com.gestiontarea.demo.domain.models.Proyecto;

public interface ProyectoRepositoryPort {
    
    Proyecto guardar(Proyecto proyecto);

    Optional<Proyecto> buscarPorId(Long id);

    List<Proyecto> listarPorPropietario(Long propietarioId);

    void eliminar(Long id);

    // TODO: agrega paginacion (Pageable / Page<Proyecto>) cuando construyas el listado general
}
