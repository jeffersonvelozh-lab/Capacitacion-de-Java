package com.gestiontarea.demo.infrastructure.adapter.in.rest;

import org.springframework.web.bind.annotation.*;

import com.gestiontarea.demo.application.port.in.GestionarProyectoUseCase;
import com.gestiontarea.demo.infrastructure.adapter.in.rest.dto.ProyectoRequest;
import com.gestiontarea.demo.infrastructure.adapter.in.rest.dto.ProyectoResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

/**
 * TODO: implementa siguiendo el patron de AuthController.
 * Piensa: ¿de donde sacas el propietarioId? Del usuario autenticado
 * (SecurityContextHolder / @AuthenticationPrincipal), no del body -- nunca
 * confies en un ID que venga del cliente para saber "quien" hace la accion.
 */
@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {
    private final GestionarProyectoUseCase gestionarProyectoUseCase;

    public ProyectoController(GestionarProyectoUseCase gestionarProyectoUseCase) {
        this.gestionarProyectoUseCase = gestionarProyectoUseCase;
    }

    @PostMapping
    public ProyectoResponse crear(@RequestBody @Valid ProyectoRequest request) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @GetMapping("/{id}")
    public ProyectoResponse obtener(@PathVariable Long id) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @GetMapping
    public List<ProyectoResponse> listar() {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @PutMapping("/{id}")
    public ProyectoResponse actualizar(@PathVariable Long id, @RequestBody @Valid ProyectoRequest request) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        throw new UnsupportedOperationException("TODO: implementar");
    }
}
