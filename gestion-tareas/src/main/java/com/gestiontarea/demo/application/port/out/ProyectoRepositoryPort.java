package com.gestiontarea.demo.application.port.out;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gestiontarea.demo.domain.models.Proyecto;

public interface ProyectoRepositoryPort {
    
    Proyecto guardar(Proyecto proyecto);

    Optional<Proyecto> buscarPorId(Long id);

    List<Proyecto> listarPorPropietario(Long propietarioId);

    Page<Proyecto> listarTodos(Pageable pageable);

    void eliminar(Long id);

}
