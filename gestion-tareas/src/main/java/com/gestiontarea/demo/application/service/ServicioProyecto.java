package com.gestiontarea.demo.application.service;

import com.gestiontarea.demo.application.port.out.ProyectoRepositoryPort;
import com.gestiontarea.demo.application.port.in.GestionarProyectoUseCase;
import com.gestiontarea.demo.domain.models.Proyecto;
import com.gestiontarea.demo.domain.models.Rol;
import com.gestiontarea.demo.exception.AccesoDenegadoException;
import com.gestiontarea.demo.exception.RecursoNoEncontradoException;

import java.util.List;

import org.springframework.stereotype.Service;

@Service 
public class ServicioProyecto implements GestionarProyectoUseCase {

    private final ProyectoRepositoryPort proyectoRepository;

    public ServicioProyecto(ProyectoRepositoryPort proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    @Override 
    public Proyecto crear(String nombre, String descripcion, Long propietarioId) {
        Proyecto proyecto = new Proyecto(null, nombre, descripcion, propietarioId);
        return proyectoRepository.guardar(proyecto);
    }

    @Override
    public Proyecto obtener(Long id, Long usuarioActualId, Rol rolUsuarioActual) {
        Proyecto proyecto = buscarOLanzar(id);
        verificarPermiso(proyecto, usuarioActualId, rolUsuarioActual);
        return proyecto;
    }

    @Override
    public List<Proyecto> listarPorPropietario(Long propietarioId, Long usuarioActualId, Rol rolUsuarioActual) {
        if (!propietarioId.equals(usuarioActualId) && rolUsuarioActual != Rol.ADMIN) {
            throw new AccesoDenegadoException("No tienes permiso para ver los proyectos de este usuario");
        }
        return proyectoRepository.listarPorPropietario(propietarioId);
    }

    @Override
    public Proyecto actualizar(Long id, String nombre, String descripcion, Long usuarioActualId, Rol rolUsuarioActual) {
        Proyecto proyecto = buscarOLanzar(id);
        verificarPermiso(proyecto, usuarioActualId, rolUsuarioActual);

        proyecto.setNombre(nombre);
        proyecto.setDescripcion(descripcion);

        return proyectoRepository.guardar(proyecto);
    }

    @Override
    public void eliminar(Long id, Long usuarioActualId, Rol rolUsuarioActual) {
        Proyecto proyecto = buscarOLanzar(id);
        verificarPermiso(proyecto, usuarioActualId, rolUsuarioActual);
        proyectoRepository.eliminar(id);
    }

    private Proyecto buscarOLanzar(Long id) {
        return proyectoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado: " + id));
    }

    private void verificarPermiso(Proyecto proyecto, Long usuarioActualId, Rol rolUsuarioActual) {
        boolean esPropietario = proyecto.getPropietarioId().equals(usuarioActualId);
        boolean esAdmin = rolUsuarioActual == Rol.ADMIN;
        if (!esPropietario && !esAdmin) {
            throw new AccesoDenegadoException("No tienes permiso sobre este proyecto");
        }
    }

    
}
