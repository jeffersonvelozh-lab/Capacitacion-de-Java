package com.gestiontarea.demo.infrastructure.adapter.out.persistence.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gestiontarea.demo.infrastructure.adapter.out.persistence.entity.UsuarioEntity;

import java.util.Optional;

/**
 * Interfaz tecnica de Spring Data — NO es el puerto de dominio.
 * El adapter UsuarioRepositoryAdapter usa esta interfaz por debajo
 * para implementar UsuarioRepositoryPort.
 */
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
