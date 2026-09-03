package com.practica.demo.Services;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.practica.demo.Dtos.PokemonDto;
import com.practica.demo.Dtos.PokemonInputDTO;
import com.practica.demo.Exceptions.PokemonNoEncontradoException;

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

    // Trae todos los pokemos que estan en memoria
    public List<PokemonDto> listar() {
        return List.copyOf(pokemones.values());
    }

    //Busca un pokemon por el id
    public PokemonDto buscarPorId(int id) {
        PokemonDto pokemon = pokemones.get(id);
        if (pokemon == null) throw new PokemonNoEncontradoException(id);
        return pokemon;
    }

    // Crea un nuevo pokemon
    public PokemonDto crear(PokemonInputDTO datos) {
        int nuevoId = contador.incrementAndGet();
        PokemonDto pokemon = new PokemonDto(nuevoId, datos.nombre(), datos.tipos(),
                datos.nivel(), datos.stats());
        pokemones.put(nuevoId, pokemon);
        return pokemon;
    }

    // Acgualiza los datos de los pokemones por el id
    public PokemonDto actualizar(int id, PokemonInputDTO datos) {
        if (!pokemones.containsKey(id)) throw new PokemonNoEncontradoException(id);
        PokemonDto actualizado = new PokemonDto(id, datos.nombre(), datos.tipos(),
                datos.nivel(), datos.stats());
        pokemones.put(id, actualizado);
        return actualizado;
    }

    public void eliminar(int id) {
        if (pokemones.remove(id) == null) throw new PokemonNoEncontradoException(id);
    }
}
