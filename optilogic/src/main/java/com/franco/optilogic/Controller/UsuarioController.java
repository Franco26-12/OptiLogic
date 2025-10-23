package com.franco.optilogic.Controller;

import com.franco.optilogic.Entity.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.franco.optilogic.Services.UsuarioService;

import dtos.RegistroDTO;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public String register(@RequestBody RegistroDTO registro) {
        Admin admin = new Admin();
        admin.setEmail(registro.getEmail());
        admin.setRol("ADMIN");
        usuarioService.registrarUsuario(admin, registro.getPassword());
        return "Administrador registrado";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Bienvenido, admin";
    }
}
