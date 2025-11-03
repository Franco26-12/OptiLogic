package com.proyecto.OptiLogic.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoEnvioDTO {
    
    @NotNull(message = "El ID del envío es requerido")
    private Long envioId;
    
    @NotBlank(message = "El nuevo estado es requerido")
    private String nuevoEstado;
    
    private String observaciones;
    private String firmaDigital;
    private String fotoEntrega;
    
    // Para escaneo QR
    private String codigoQR;
    private boolean confirmadoPorQR = false;
}
