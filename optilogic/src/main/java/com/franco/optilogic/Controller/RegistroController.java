package com.franco.optilogic.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.ui.Model;

import com.franco.optilogic.Services.UsuarioService;

import dtos.RegistroDTO;

@Controller
public class RegistroController {

    private final UsuarioService usuarioService;

    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("registroUsuarioDto", new RegistroDTO());
        return "registro"; //
    }

@PostMapping("/registro")
public ResponseEntity<String> registrarUsuario(@RequestBody RegistroDTO registroDto) {
    usuarioService.registrarNuevoUsuario(registroDto);
    return ResponseEntity.ok("Usuario registrado con éxito");
}
}
