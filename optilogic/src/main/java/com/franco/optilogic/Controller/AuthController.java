package com.franco.optilogic.Controller;

import com.franco.optilogic.Config.JwtTokenProvider;
import com.franco.optilogic.Entity.Usuario; // 
import com.franco.optilogic.Entity.Admin;
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

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.usuarioService = usuarioService;
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
           
            Admin usuario = new Admin();
            usuario.setEmail(registroDto.getEmail());

            usuario.setRol("ADMIN"); 

            usuarioService.registrarUsuario(usuario, registroDto.getPassword());

            Map<String, Object> body = new HashMap<>();
            body.put("message", "Usuario registrado exitosamente.");
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
