package com.proyecto.OptiLogic.controllers;

import com.proyecto.OptiLogic.entities.Cliente;
import com.proyecto.OptiLogic.entities.Repartidor;
import com.proyecto.OptiLogic.entities.Usuario;
import com.proyecto.OptiLogic.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // GET /api/usuarios -> Obtener todos los usuarios
    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioService.obtenerTodosLosUsuarios();
    }

    // Para crear un usuario, necesitamos endpoints separados que reciban el tipo correcto.

    // POST /api/usuarios/cliente -> Crear un nuevo Cliente
    @PostMapping("/cliente")
    public Usuario crearCliente(@RequestBody Cliente cliente) {
        return usuarioService.guardarUsuario(cliente);
    }

    // POST /api/usuarios/repartidor -> Crear un nuevo Repartidor
    @PostMapping("/repartidor")
    public Usuario crearRepartidor(@RequestBody Repartidor repartidor) {
        return usuarioService.guardarUsuario(repartidor);
    }
    
    // GET /api/usuarios/1 -> Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}