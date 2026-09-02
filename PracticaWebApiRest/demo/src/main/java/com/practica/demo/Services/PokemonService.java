package com.practica.demo.Services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.practica.demo.Dtos.PokemonDto;
import com.practica.demo.Dtos.PokemonInputDTO;

@Service
public class PokemonService {
    private final Map<Integer, PokemonDto> pokemones = new ConcurrentHashMap<>();
    private final AtomicInteger contador = new AtomicInteger();

    public PokemonService() {
        crear(new PokemonInputDTO("Pikachu", List.of("Eléctrico"), 17,
                new PokemonDto.Stats(35, 55, 40, 90)));
        crear(new PokemonInputDTO("Charmander", List.of("Fuego"), 12,
                new PokemonDto.Stats(39, 52, 43, 65)));
    }

    public List<PokemonDto> listar() {
        return List.copyOf(pokemones.values());
    }

    public Optional<PokemonDto> buscarPorId(int id) {
        return Optional.ofNullable(pokemones.get(id));
    }

    // En PokemonService.java
    public PokemonDto crear(PokemonInputDTO datos) {
        int nuevoId = contador.incrementAndGet();
        PokemonDto pokemon = new PokemonDto(nuevoId, datos.nombre(), datos.tipos(),
                datos.nivel(), datos.stats());
        pokemones.put(nuevoId, pokemon);
        return pokemon;
    }

    public Optional<PokemonDto> actualizar(int id, PokemonInputDTO datos) {
        if (!pokemones.containsKey(id)) return Optional.empty();
        PokemonDto actualizado = new PokemonDto(id, datos.nombre(), datos.tipos(),
                datos.nivel(), datos.stats());
        pokemones.put(id, actualizado);
        return Optional.of(actualizado);
    }

    public boolean eliminar(int id) {
        return pokemones.remove(id) != null;
    }
}
