package com.gestiontarea.demo.infrastructure.adapter.in.rest;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestiontarea.demo.application.port.in.GestionarTareaUseCase;
import com.gestiontarea.demo.infrastructure.adapter.in.rest.dto.TareaRequest;
import com.gestiontarea.demo.infrastructure.adapter.in.rest.dto.TareaResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

/**
 * TODO: implementa siguiendo el patron de AuthController.
 */
@RestController 
@RequestMapping("/api/tareas")
public class TareaController {

    private final GestionarTareaUseCase gestionarTareaUseCase;

    public TareaController(GestionarTareaUseCase gestionarTareaUseCase){
        this.gestionarTareaUseCase = gestionarTareaUseCase;
    }

    @PostMapping 
    public TareaResponse crear(@RequestBody @Valid TareaRequest request){
        throw new UnsupportedOperationException("TODO: implementar");
    }
    
    @GetMapping("/{id}")
    public TareaResponse obtener(@PathVariable Long id) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @GetMapping
    public List<TareaResponse> listarPorProyecto(@RequestParam Long proyectoId) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @PatchMapping("/{id}/asignar")
    public TareaResponse asignar(@PathVariable Long id, @RequestParam Long usuarioId) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @PatchMapping("/{id}/estado")
    public TareaResponse cambiarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        throw new UnsupportedOperationException("TODO: implementar");
    }
}
