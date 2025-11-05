package com.franco.optilogic.Controller;

import com.franco.optilogic.Config.JwtTokenProvider;
import com.franco.optilogic.Entity.Usuario; // 
import com.franco.optilogic.Entity.Admin;
import com.franco.optilogic.Repository.AdminRepository;
import com.franco.optilogic.Services.UsuarioService;

import dtos.JwtAuthResponseDTO;
import dtos.LoginDto;
import dtos.RegistroDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException; // Para manejar errores de DB
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider; 
    private final UsuarioService usuarioService;
    private final AdminRepository adminRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, 
                         UsuarioService usuarioService, AdminRepository adminRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.usuarioService = usuarioService;
        this.adminRepository = adminRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDTO> login(@RequestBody LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

       
        String role = authentication.getAuthorities().stream()
                        .findFirst() 
                        .map(a -> a.getAuthority())
                        .orElse("UNKNOWN"); 

        String token = tokenProvider.generateToken(authentication, role);

        return ResponseEntity.ok(new JwtAuthResponseDTO(token, role));
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegistroDTO registroDto) {
        
        try {
            // Validar que sea el primer admin (si no hay admins en el sistema)
            long adminCount = adminRepository.count();
            
            if (adminCount > 0) {
                // Ya existe al menos un admin, no se permite registro público
                Map<String, Object> body = new HashMap<>();
                body.put("error", "Registro no permitido");
                body.put("message", "El sistema ya tiene un administrador. Use /api/admin/usuarios para crear más usuarios.");
                return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
            }
            
            // Es el primer admin, permitir registro
            Admin usuario = new Admin();
            usuario.setNombre(registroDto.getNombre());
            usuario.setApellido(registroDto.getApellido());
            usuario.setCedula(registroDto.getCedula());
            usuario.setEmail(registroDto.getEmail());
            usuario.setRol("ADMIN"); 

            usuarioService.registrarUsuario(usuario, registroDto.getPassword());

            Map<String, Object> body = new HashMap<>();
            body.put("message", "Primer administrador registrado exitosamente.");
            body.put("email", usuario.getEmail());
            body.put("role", usuario.getRol());
            return new ResponseEntity<>(body, HttpStatus.CREATED);

        } catch (DataIntegrityViolationException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Error de registro");
            body.put("message", "Error de registro: El email ya está en uso.");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Error de registro");
            body.put("message", e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace(); 
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Error interno");
            body.put("message", "Error interno del servidor al registrar.");
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
