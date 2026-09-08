package com.gestiontarea.demo.infrastructure.adapter.out.persistence.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.ProyectoEntity;

import java.util.List;

public interface ProyectoJpaRepository extends JpaRepository<ProyectoEntity, Long> {

    List<ProyectoEntity> findByPropietarioId(Long propietarioId);
}
