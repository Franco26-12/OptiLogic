package com.proyecto.OptiLogic.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class EnvioDTO {
    
    private Long id;
    private String numeroSeguimiento;
    private String codigoQR;
    
    private String direccionOrigen;
    private String direccionDestino;
    private String ciudadDestino;
    private String codigoPostalDestino;
    
    private String estado;
    private String observaciones;
    private BigDecimal costoEnvio;
    private Integer prioridad;
    
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaEntregaEstimada;
    private LocalDateTime fechaEntregaReal;
    private LocalDateTime fechaConfirmacionCliente;
    
    private String firmaDigital;
    private String fotoEntrega;
    
    private Long clienteId;
    private String clienteNombre;
    private String clienteCedula;
    
    private Long repartidorId;
    private String repartidorNombre;
    private String repartidorCedula;
    
    private Long adminCreadorId;
    private String adminCreadorNombre;
    
    private List<ProductoEnvioDTO> productos;
    private Map<Long, Integer> cantidadPorProducto;
    
    @Data
    public static class ProductoEnvioDTO {
        private Long id;
        private String nombre;
        private String sku;
        private String codigoQR;
        private Integer cantidad;
    }
}
