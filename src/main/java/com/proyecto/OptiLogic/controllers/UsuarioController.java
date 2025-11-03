package com.proyecto.OptiLogic.controllers;

import com.proyecto.OptiLogic.dtos.RegistroDTO;
import com.proyecto.OptiLogic.entities.Admin;
import com.proyecto.OptiLogic.entities.Cliente;
import com.proyecto.OptiLogic.entities.Repartidor;
import com.proyecto.OptiLogic.entities.Usuario;
import com.proyecto.OptiLogic.services.AuthService;
import com.proyecto.OptiLogic.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }
    
    @GetMapping("/admins")
    public ResponseEntity<List<Admin>> listarAdmins() {
        List<Admin> admins = usuarioService.obtenerTodosLosUsuarios().stream()
                .filter(u -> u instanceof Admin)
                .map(u -> (Admin) u)
                .collect(Collectors.toList());
        return ResponseEntity.ok(admins);
    }
    
    @GetMapping("/repartidores")
    public ResponseEntity<List<Repartidor>> listarRepartidores() {
        List<Repartidor> repartidores = usuarioService.obtenerTodosLosUsuarios().stream()
                .filter(u -> u instanceof Repartidor)
                .map(u -> (Repartidor) u)
                .collect(Collectors.toList());
        return ResponseEntity.ok(repartidores);
    }
    
    @GetMapping("/clientes")
    public ResponseEntity<List<Cliente>> listarClientes() {
        List<Cliente> clientes = usuarioService.obtenerTodosLosUsuarios().stream()
                .filter(u -> u instanceof Cliente)
                .map(u -> (Cliente) u)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientes);
    }

    // Admin crea otro admin
    @PostMapping("/admin")
    public ResponseEntity<?> crearAdmin(@Valid @RequestBody RegistroDTO registroDTO) {
        try {
            Map<String, Object> response = authService.registrarAdmin(registroDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Admin crea un repartidor
    @PostMapping("/repartidor")
    public ResponseEntity<?> crearRepartidor(@Valid @RequestBody RegistroDTO registroDTO) {
        try {
            Map<String, Object> response = authService.registrarRepartidor(registroDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Admin crea un cliente
    @PostMapping("/cliente")
    public ResponseEntity<?> crearCliente(@Valid @RequestBody RegistroDTO registroDTO) {
        try {
            Map<String, Object> response = authService.registrarCliente(registroDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody Usuario usuarioActualizado) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setApellido(usuarioActualizado.getApellido());
            usuario.setTelefono(usuarioActualizado.getTelefono());
            usuario.setEmail(usuarioActualizado.getEmail());
            
            Usuario guardado = usuarioService.guardarUsuario(usuario);
            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}