package com.proyecto.OptiLogic.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaDTO {
    
    private Long id;
    
    @NotBlank(message = "El nombre de la categoría es requerido")
    private String nombre;
    
    private String descripcion;
    private String icono;
    private boolean activo = true;
    private Integer cantidadProductos;
}
