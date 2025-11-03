package com.proyecto.OptiLogic.controllers;

import com.proyecto.OptiLogic.dtos.*;
import com.proyecto.OptiLogic.entities.Envio;
import com.proyecto.OptiLogic.services.EnvioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/envios")
@CrossOrigin(origins = "*")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @GetMapping
    public ResponseEntity<List<EnvioDTO>> listarTodos() {
        List<EnvioDTO> envios = envioService.obtenerTodosLosEnvios().stream()
                .map(envioService::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(envios);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EnvioDTO> obtenerPorId(@PathVariable Long id) {
        return envioService.obtenerEnvioPorId(id)
                .map(envio -> ResponseEntity.ok(envioService.convertirADTO(envio)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/tracking/{numeroSeguimiento}")
    public ResponseEntity<EnvioDTO> obtenerPorNumeroSeguimiento(@PathVariable String numeroSeguimiento) {
        return envioService.obtenerEnvioPorNumeroSeguimiento(numeroSeguimiento)
                .map(envio -> ResponseEntity.ok(envioService.convertirADTO(envio)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/qr/{codigoQR}")
    public ResponseEntity<EnvioDTO> obtenerPorCodigoQR(@PathVariable String codigoQR) {
        return envioService.obtenerEnvioPorCodigoQR(codigoQR)
                .map(envio -> ResponseEntity.ok(envioService.convertirADTO(envio)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/repartidor/{repartidorId}")
    public ResponseEntity<List<EnvioDTO>> listarPorRepartidor(@PathVariable Long repartidorId) {
        List<EnvioDTO> envios = envioService.obtenerEnviosPorRepartidor(repartidorId).stream()
                .map(envioService::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(envios);
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<EnvioDTO>> listarPorCliente(@PathVariable Long clienteId) {
        List<EnvioDTO> envios = envioService.obtenerEnviosPorCliente(clienteId).stream()
                .map(envioService::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(envios);
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<EnvioDTO>> listarPorEstado(@PathVariable String estado) {
        try {
            Envio.EstadoEnvio estadoEnum = Envio.EstadoEnvio.valueOf(estado);
            List<EnvioDTO> envios = envioService.obtenerEnviosPorEstado(estadoEnum).stream()
                    .map(envioService::convertirADTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(envios);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @Valid @RequestBody CrearEnvioDTO crearEnvioDTO,
            @RequestParam Long adminId) {
        try {
            Envio envio = envioService.crearEnvio(crearEnvioDTO, adminId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(envioService.convertirADTO(envio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/estado")
    public ResponseEntity<?> actualizarEstado(@Valid @RequestBody ActualizarEstadoEnvioDTO dto) {
        try {
            Envio envio = envioService.actualizarEstadoEnvio(dto);
            return ResponseEntity.ok(envioService.convertirADTO(envio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{envioId}/asignar-repartidor")
    public ResponseEntity<?> asignarRepartidor(
            @PathVariable Long envioId,
            @RequestParam Long repartidorId) {
        try {
            Envio envio = envioService.asignarRepartidor(envioId, repartidorId);
            return ResponseEntity.ok(envioService.convertirADTO(envio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/escanear-qr")
    public ResponseEntity<?> escanearQR(@RequestBody Map<String, String> request) {
        try {
            String codigoQR = request.get("codigoQR");
            String nuevoEstado = request.get("nuevoEstado");
            
            // Buscar envío por código QR
            Envio envio = envioService.obtenerEnvioPorCodigoQR(codigoQR)
                    .orElseThrow(() -> new RuntimeException("Envío no encontrado con este QR"));
            
            // Actualizar estado
            ActualizarEstadoEnvioDTO dto = new ActualizarEstadoEnvioDTO();
            dto.setEnvioId(envio.getId());
            dto.setNuevoEstado(nuevoEstado);
            dto.setCodigoQR(codigoQR);
            dto.setConfirmadoPorQR(true);
            dto.setObservaciones("Estado actualizado mediante escaneo QR");
            
            if ("ENTREGADO".equals(nuevoEstado)) {
                dto.setFirmaDigital(request.get("firmaDigital"));
                dto.setFotoEntrega(request.get("fotoEntrega"));
            }
            
            Envio envioActualizado = envioService.actualizarEstadoEnvio(dto);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Estado actualizado exitosamente",
                    "envio", envioService.convertirADTO(envioActualizado)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> stats = Map.of(
                "total", envioService.obtenerTodosLosEnvios().size(),
                "pendientes", envioService.obtenerEnviosPorEstado(Envio.EstadoEnvio.PENDIENTE).size(),
                "enTransito", envioService.obtenerEnviosPorEstado(Envio.EstadoEnvio.EN_TRANSITO).size(),
                "entregados", envioService.obtenerEnviosPorEstado(Envio.EstadoEnvio.ENTREGADO).size(),
                "cancelados", envioService.obtenerEnviosPorEstado(Envio.EstadoEnvio.CANCELADO).size()
        );
        return ResponseEntity.ok(stats);
    }
}