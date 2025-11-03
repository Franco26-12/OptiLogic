package com.proyecto.OptiLogic.controllers;

import com.proyecto.OptiLogic.dtos.*;
import com.proyecto.OptiLogic.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            Map<String, Object> response = authService.login(loginDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/registro")
    public ResponseEntity<?> registrarAdmin(@Valid @RequestBody RegistroDTO registroDTO) {
        try {
            Map<String, Object> response = authService.registrarAdmin(registroDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(
            @RequestParam Long usuarioId,
            @RequestParam String passwordActual,
            @RequestParam String passwordNuevo) {
        try {
            authService.cambiarPassword(usuarioId, passwordActual, passwordNuevo);
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/resetear-password")
    public ResponseEntity<?> resetearPassword(
            @RequestParam String cedula,
            @RequestParam String nuevaPasswordTemporal) {
        try {
            authService.resetearPassword(cedula, nuevaPasswordTemporal);
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña reseteada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/validar-token")
    public ResponseEntity<?> validarToken(@RequestHeader("Authorization") String token) {
        try {
            String tokenLimpio = token.replace("Bearer ", "");
            authService.validarToken(tokenLimpio);
            return ResponseEntity.ok(Map.of("valido", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valido", false, "error", e.getMessage()));
        }
    }
}
