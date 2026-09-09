package com.gestiontarea.demo.infrastructure.adapter.in.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestiontarea.demo.application.port.in.GestionarTareaUseCase;
import com.gestiontarea.demo.application.port.out.UsuarioAutenticado;
import com.gestiontarea.demo.domain.models.EstadoTarea;
import com.gestiontarea.demo.domain.models.Rol;
import com.gestiontarea.demo.domain.models.Tarea;
import com.gestiontarea.demo.infrastructure.adapter.in.rest.dto.TareaRequest;
import com.gestiontarea.demo.infrastructure.adapter.in.rest.dto.TareaResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/tareas")
public class TareaController {

    private final GestionarTareaUseCase gestionarTareaUseCase;

    public TareaController(GestionarTareaUseCase gestionarTareaUseCase){
        this.gestionarTareaUseCase = gestionarTareaUseCase;
    }

    @PostMapping 
    public ResponseEntity<TareaResponse> crear(@RequestBody @Valid TareaRequest request){
        Tarea creada = gestionarTareaUseCase.crear(
            request.titulo(), request.descripcion(), request.proyectoId(),
            usuarioActualId(), rolUsuarioActual());

        return ResponseEntity.status(HttpStatus.CREATED).body(TareaResponse.from(creada));
    }
    
    @GetMapping("/{id}")
    public TareaResponse obtener(@PathVariable Long id) {
        Tarea tarea = gestionarTareaUseCase.obtener(id);
        return TareaResponse.from(tarea);
    }

    @GetMapping
    public List<TareaResponse> listarPorProyecto(@RequestParam Long proyectoId) {
        return  gestionarTareaUseCase.listarPorProyecto(proyectoId).stream()
            .map(TareaResponse::from)
            .collect(Collectors.toList());
    }

    @PatchMapping("/{id}/asignar")
    public TareaResponse asignar(@PathVariable Long id, @RequestParam Long usuarioId) {
        Tarea tarea = gestionarTareaUseCase.asignar(id, usuarioId, usuarioActualId(), rolUsuarioActual());
        return TareaResponse.from(tarea);
    }

    @PatchMapping("/{id}/estado")
    public TareaResponse cambiarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        EstadoTarea estado = EstadoTarea.valueOf(nuevoEstado.toUpperCase());
        Tarea tarea = gestionarTareaUseCase.cambiarEstado(id, estado);
        return TareaResponse.from(tarea);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        gestionarTareaUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioAutenticado usuarioActual(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UsuarioAutenticado) auth.getPrincipal();
    }

    private Long usuarioActualId() {
        return usuarioActual().id();
    }

    private Rol rolUsuarioActual() {
        return usuarioActual().rol();
    }
}
