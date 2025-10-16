package com.franco.optilogic.Controller;



import dtos.CrearEnvioDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.franco.optilogic.Entity.Envio;
import com.franco.optilogic.Services.EnvioService;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {
    @Autowired private EnvioService envioService;

    @GetMapping
    public List<Envio> listarTodos() { return envioService.obtenerTodosLosEnvios(); }

    @PostMapping
    public Envio crearEnvio(@RequestBody CrearEnvioDTO dto) { return envioService.crearEnvio(dto); }

    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerEnvio(@PathVariable Long id) {
        return envioService.obtenerEnvioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
