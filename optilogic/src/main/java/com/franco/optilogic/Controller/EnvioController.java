package com.franco.optilogic.Controller;



import dtos.CrearEnvioDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.franco.optilogic.Entity.Envio;
import com.franco.optilogic.Services.EnvioService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {
    @Autowired private EnvioService envioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Envio> listarTodos() { 
        return envioService.obtenerTodosLosEnvios(); 
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> crearEnvio(@RequestBody CrearEnvioDTO dto) { 
        try {
            Envio envio = envioService.crearEnvio(dto);
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Envío creado exitosamente");
            body.put("envio", envio);
            body.put("codigoQR", envio.getCodigoQR());
            return new ResponseEntity<>(body, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Error al crear envío");
            body.put("message", e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerEnvio(@PathVariable Long id) {
        return envioService.obtenerEnvioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Envio>> obtenerEnviosPendientes() {
        List<Envio> enviosPendientes = envioService.obtenerEnviosPendientes();
        return ResponseEntity.ok(enviosPendientes);
    }
    
    @GetMapping("/repartidor/{repartidorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPARTIDOR')")
    public ResponseEntity<List<Envio>> obtenerEnviosPorRepartidor(@PathVariable Long repartidorId) {
        try {
            List<Envio> envios = envioService.obtenerEnviosPorRepartidor(repartidorId);
            return ResponseEntity.ok(envios);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{envioId}/asignar/{repartidorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> asignarRepartidor(
            @PathVariable Long envioId, 
            @PathVariable Long repartidorId) {
        try {
            Envio envio = envioService.asignarRepartidor(envioId, repartidorId);
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Repartidor asignado exitosamente");
            body.put("envio", envio);
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Error al asignar repartidor");
            body.put("message", e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/confirmar-entrega")
    @PreAuthorize("hasRole('REPARTIDOR')")
    public ResponseEntity<Map<String, Object>> confirmarEntrega(@RequestBody Map<String, String> request) {
        try {
            String codigoQR = request.get("codigoQR");
            Envio envio = envioService.confirmarEntrega(codigoQR);
            
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Entrega confirmada exitosamente");
            body.put("envio", envio);
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Error al confirmar entrega");
            body.put("message", e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }
}
