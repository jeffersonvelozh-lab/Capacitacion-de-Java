package com.practica.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practica.demo.Entities.Pokemon;

public interface PokemonRepository extends JpaRepository<Pokemon, Integer> {
    
}
