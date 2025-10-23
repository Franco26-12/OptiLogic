package com.franco.optilogic.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.franco.optilogic.Entity.Usuario;
import com.franco.optilogic.Entity.UsuarioConcreto;
import com.franco.optilogic.Services.UsuarioService;

import dtos.RegistroDTO;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegistroDTO registro) {
        Usuario usuario = new UsuarioConcreto();
        usuario.setEmail(registro.getEmail());
        usuario.setRol("ADMIN");  // Solo para pruebas asignamos admin directamente
        usuarioService.registrarUsuario(usuario, registro.getPassword());
        return "Usuario registrado";
    }
}
