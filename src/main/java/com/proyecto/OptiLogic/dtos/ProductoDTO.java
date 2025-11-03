package com.proyecto.OptiLogic.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoDTO {
    
    private Long id;
    
    @NotBlank(message = "El nombre del producto es requerido")
    private String nombre;
    
    @NotBlank(message = "El SKU es requerido")
    private String sku;
    
    private String descripcion;
    
    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal precio;
    
    @NotNull(message = "El stock es requerido")
    @Min(0)
    private Integer stockActual;
    
    private Integer stockMinimo;
    
    @NotNull(message = "El peso es requerido")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal peso;
    
    private String dimensiones;
    private String ubicacionAlmacen;
    
    @NotNull(message = "La categoría es requerida")
    private Long categoriaId;
    
    private String categoriaNombre;
    private String codigoQR;
    private boolean activo = true;
}
