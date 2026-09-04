package com.practica.demo.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.practica.demo.Dtos.PokemonDto;
import com.practica.demo.Dtos.PokemonInputDTO;
import com.practica.demo.Entities.Pokemon;
import com.practica.demo.Exceptions.PokemonNoEncontradoException;
import com.practica.demo.Repositories.PokemonRepository;

@Service
public class PokemonService {

    private final PokemonRepository repository;

    public PokemonService(PokemonRepository repository) {
        this.repository = repository;
    }

    public List<PokemonDto> listar() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public PokemonDto buscarPorId(int id) {
        Pokemon pokemon = repository.findById(id)
                .orElseThrow(() -> new PokemonNoEncontradoException(id));
        return toDto(pokemon);
    }

    public PokemonDto crear(PokemonInputDTO datos) {
        Pokemon nuevo = new Pokemon(
                datos.nombre(),
                datos.tipos(),
                datos.nivel(),
                toEntityStats(datos.stats())
        );
        return toDto(repository.save(nuevo));
    }

    public PokemonDto actualizar(int id, PokemonInputDTO datos) {
        Pokemon existente = repository.findById(id)
                .orElseThrow(() -> new PokemonNoEncontradoException(id));

        existente.setNombre(datos.nombre());
        existente.setTipos(datos.tipos());
        existente.setNivel(datos.nivel());
        existente.setStats(toEntityStats(datos.stats()));

        return toDto(repository.save(existente));
    }

    public void eliminar(int id) {
        if (!repository.existsById(id)) throw new PokemonNoEncontradoException(id);
        repository.deleteById(id);
    }

    // --- Mapeo Entity <-> DTO ---

    private PokemonDto toDto(Pokemon p) {
        return new PokemonDto(
                p.getId(),
                p.getNombre(),
                p.getTipos(),
                p.getNivel(),
                new PokemonDto.Stats(
                        p.getStats().getVida(),
                        p.getStats().getAtaque(),
                        p.getStats().getDefensa(),
                        p.getStats().getVelocidad()
                )
        );
    }

    private Pokemon.Stats toEntityStats(PokemonDto.Stats s) {
        return new Pokemon.Stats(s.vida(), s.ataque(), s.defensa(), s.velocidad());
    }
}