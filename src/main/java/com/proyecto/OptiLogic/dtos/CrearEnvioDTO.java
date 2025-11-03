package com.proyecto.OptiLogic.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class CrearEnvioDTO {
    
    @NotNull(message = "El cliente es requerido")
    private Long clienteId;
    
    private Long repartidorId; // Opcional, se puede asignar después
    
    @NotBlank(message = "La dirección de origen es requerida")
    private String direccionOrigen;
    
    @NotBlank(message = "La dirección de destino es requerida")
    private String direccionDestino;
    
    @NotBlank(message = "La ciudad de destino es requerida")
    private String ciudadDestino;
    
    private String codigoPostalDestino;
    
    @NotNull(message = "El costo de envío es requerido")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal costoEnvio;
    
    @Min(1)
    @Max(3)
    private Integer prioridad = 1;
    
    private LocalDateTime fechaEntregaEstimada;
    
    private String observaciones;
    
    @NotEmpty(message = "Debe seleccionar al menos un producto")
    private Map<Long, Integer> productosConCantidad; // ProductoId -> Cantidad
}