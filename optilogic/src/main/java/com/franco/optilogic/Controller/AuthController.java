package com.franco.optilogic.Controller;

import com.franco.optilogic.Config.JwtTokenProvider;
import com.franco.optilogic.Entity.Admin;
import com.franco.optilogic.Services.UsuarioService;

import dtos.JwtAuthResponseDTO;
import dtos.LoginDto;
import dtos.RegistroDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager; // Importar
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Inyectar el gestor de autenticación de Spring Security
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider; // Inyectar nuestro generador de token
    private final UsuarioService usuarioService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.usuarioService = usuarioService;
    }

    // 🚨 NUEVO ENDPOINT DE LOGIN: POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDTO> login(@RequestBody LoginDto loginDto) {

        // 1. Usar AuthenticationManager para validar las credenciales
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        // 2. Si la validación es exitosa, establecer la autenticación en el contexto
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. 🚨 Obtener el rol del usuario autenticado (Asumimos que el AdminService lo proporciona)
        // NOTA: Para este ejemplo, asumimos que el rol es ADMIN, pero debe venir de la DB.
        String role = "ADMIN"; // Obtendríamos el rol real aquí si tuviéramos UserDetailsService

        // 4. Generar el Token JWT
        String token = tokenProvider.generateToken(authentication, role);

        // 5. Devolver la respuesta con el token y el rol
        return ResponseEntity.ok(new JwtAuthResponseDTO(token, role));
    }
    
    // ... (El método @PostMapping("/register") y otros quedan igual) ...

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistroDTO registroDto) {
        try {
            // ... (código de registro) ...
            Admin admin = new Admin();
            admin.setEmail(registroDto.getEmail());
            admin.setRol("ADMIN"); 

            usuarioService.registrarUsuario(admin, registroDto.getPassword());

            return new ResponseEntity<>("Administrador registrado exitosamente.", HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("Error al registrar: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}