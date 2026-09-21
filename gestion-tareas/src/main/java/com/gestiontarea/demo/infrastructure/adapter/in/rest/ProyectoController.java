package com.gestiontarea.demo.infrastructure.adapter.in.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.gestiontarea.demo.application.port.in.GestionarProyectoUseCase;
import com.gestiontarea.demo.application.port.out.UsuarioAutenticado;
import com.gestiontarea.demo.domain.models.Proyecto;
import com.gestiontarea.demo.domain.models.Rol;
import com.gestiontarea.demo.infrastructure.adapter.in.rest.dto.ProyectoRequest;
import com.gestiontarea.demo.infrastructure.adapter.in.rest.dto.ProyectoResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {
    private final GestionarProyectoUseCase gestionarProyectoUseCase;

    public ProyectoController(GestionarProyectoUseCase gestionarProyectoUseCase) {
        this.gestionarProyectoUseCase = gestionarProyectoUseCase;
    }

    @PostMapping
    public ResponseEntity<ProyectoResponse> crear(@RequestBody ProyectoRequest request) {
        Proyecto creado = gestionarProyectoUseCase.crear(
                request.nombre(), request.descripcion(), usuarioActualId());

        return ResponseEntity.status(HttpStatus.CREATED).body(ProyectoResponse.from(creado));
    }

    @GetMapping("/{id}")
    public ProyectoResponse obtener(@PathVariable Long id) {
        Proyecto proyecto = gestionarProyectoUseCase.obtener(id, usuarioActualId(), rolUsuarioActual());
        return ProyectoResponse.from(proyecto);
    }

    @GetMapping
    public List<ProyectoResponse> listar() {
        // Asumo que "listar" sin parametros = "mis proyectos" (propietarioId = usuario actual).
        // Si necesitas que un ADMIN liste los de otro usuario, habria que agregar
        // un @RequestParam Long propietarioId opcional.
        List<Proyecto> proyectos = gestionarProyectoUseCase.listarPorPropietario(
                usuarioActualId(), usuarioActualId(), rolUsuarioActual());

        return proyectos.stream()
                .map(ProyectoResponse::from)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ProyectoResponse actualizar(@PathVariable Long id, @RequestBody @Valid ProyectoRequest request) {
        Proyecto actualizado = gestionarProyectoUseCase.actualizar(
                id, request.nombre(), request.descripcion(), usuarioActualId(), rolUsuarioActual());

        return ProyectoResponse.from(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        gestionarProyectoUseCase.eliminar(id, usuarioActualId(), rolUsuarioActual());
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
