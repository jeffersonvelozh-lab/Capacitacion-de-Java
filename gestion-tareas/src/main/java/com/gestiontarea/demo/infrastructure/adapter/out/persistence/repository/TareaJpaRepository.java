package com.gestiontarea.demo.infrastructure.adapter.out.persistence.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.TareaEntity;

import java.util.List;

public interface TareaJpaRepository extends JpaRepository<TareaEntity, Long> {

    List<TareaEntity> findByProyectoId(Long proyectoId);

    List<TareaEntity> findByAsignadoAId(Long usuarioId);
}
