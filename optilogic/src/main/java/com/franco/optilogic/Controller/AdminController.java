package com.franco.optilogic.Controller;

import com.franco.optilogic.Entity.Repartidor;
import com.franco.optilogic.Entity.Usuario;
import com.franco.optilogic.Services.DashboardService;
import com.franco.optilogic.Services.UsuarioService;
import dtos.DashboardStatsDTO;
import dtos.GestionUsuarioDTO;
import dtos.UsuarioAdminDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final DashboardService dashboardService;
    private final UsuarioService usuarioService;

    @Autowired
    public AdminController(DashboardService dashboardService, UsuarioService usuarioService) {
        this.dashboardService = dashboardService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStatsDTO> obtenerEstadisticasDashboard() {
        DashboardStatsDTO stats = dashboardService.obtenerEstadisticas();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioAdminDTO>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        List<UsuarioAdminDTO> response = usuarios.stream()
                .map(this::mapearUsuarioDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> crearUsuario(@RequestBody GestionUsuarioDTO dto) {
        try {
            Usuario nuevo = usuarioService.crearUsuario(dto);
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Usuario creado exitosamente.");
            body.put("usuario", mapearUsuarioDto(nuevo));
            return new ResponseEntity<>(body, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Error al crear usuario");
            body.put("message", e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> actualizarUsuario(@PathVariable Long id, @RequestBody GestionUsuarioDTO dto) {
        try {
            Usuario actualizado = usuarioService.actualizarUsuario(id, dto);
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Usuario actualizado exitosamente.");
            body.put("usuario", mapearUsuarioDto(actualizado));
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Error al actualizar usuario");
            body.put("message", e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Usuario eliminado exitosamente.");
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Error al eliminar usuario");
            body.put("message", e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    private UsuarioAdminDTO mapearUsuarioDto(Usuario usuario) {
        String estado = null;
        if (usuario instanceof Repartidor repartidor) {
            estado = repartidor.getEstado();
        }

        UsuarioAdminDTO dto = new UsuarioAdminDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCedula(usuario.getCedula());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        dto.setEstado(estado);
        return dto;
    }
}

