package com.proyecto.OptiLogic.controllers;

import com.proyecto.OptiLogic.dtos.CrearEnvioDTO;
import com.proyecto.OptiLogic.entities.Envio;
import com.proyecto.OptiLogic.services.EnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    // GET /api/envios -> Obtener todos los envíos
    @GetMapping
    public List<Envio> listarTodos() {
        return envioService.obtenerTodosLosEnvios();
    }

    // POST /api/envios -> Crear un nuevo envío
    @PostMapping
    public Envio crearEnvio(@RequestBody CrearEnvioDTO crearEnvioDTO) {
        return envioService.crearEnvio(crearEnvioDTO);
    }

    // GET /api/envios/1 -> Obtener un envío por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerEnvio(@PathVariable Long id) {
        return envioService.obtenerEnvioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}