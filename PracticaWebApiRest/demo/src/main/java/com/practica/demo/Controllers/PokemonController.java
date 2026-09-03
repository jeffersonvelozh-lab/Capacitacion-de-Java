package com.practica.demo.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practica.demo.Dtos.MensajeResponse;
import com.practica.demo.Dtos.PokemonDto;
import com.practica.demo.Dtos.PokemonInputDTO;
import com.practica.demo.Services.PokemonService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/pokemones")
public class PokemonController {

    private final PokemonService service;

    public PokemonController(PokemonService service) {
        this.service = service;
    }

    @GetMapping 
    public List<PokemonDto> listar() { 
        return service.listar(); 
    }

    @GetMapping("/{id}")
    public PokemonDto buscarPorId(@PathVariable int id) {
        return service.buscarPorId(id);
    }


    @PostMapping
    public ResponseEntity<PokemonDto> crear(@RequestBody PokemonInputDTO datos) {
        PokemonDto creado = service.crear(datos);
        return ResponseEntity.status(201).body(creado);
    }


    @PutMapping("/{id}")
    public PokemonDto actualizar (@PathVariable int id, @RequestBody PokemonInputDTO datos ) {
        return service.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public MensajeResponse eliminar(@PathVariable int id) {
        service.eliminar(id);
        return new MensajeResponse("Pokemon con el id " + id + " eliminado correctamente");
    }


}
