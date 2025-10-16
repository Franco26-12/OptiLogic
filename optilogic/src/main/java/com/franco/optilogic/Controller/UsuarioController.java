package com.franco.optilogic.Controller;

import com.franco.optilogic.Entity.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.franco.optilogic.Entity.Admin;
import com.franco.optilogic.Entity.Cliente;
import com.franco.optilogic.Entity.Usuario;
import com.franco.optilogic.Services.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listarTodos() { return usuarioService.obtenerTodosLosUsuarios(); }

    @PostMapping("/cliente")
    public Usuario crearCliente(@RequestBody Cliente cliente) { return usuarioService.guardarUsuario(cliente); }

    @PostMapping("/repartidor")
    public Usuario crearRepartidor(@RequestBody Repartidor repartidor) { return usuarioService.guardarUsuario(repartidor); }
    
    @PostMapping("/admin")
    public Usuario crearAdmin(@RequestBody Admin admin) { return usuarioService.guardarUsuario(admin); }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}