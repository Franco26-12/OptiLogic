package com.proyecto.OptiLogic.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RepartidorDTO {
    
    private Long id;
    
    @NotBlank(message = "La cédula es requerida")
    @Pattern(regexp = "^[0-9]{6,12}$", message = "La cédula debe contener solo números")
    private String cedula;
    
    @NotBlank(message = "El nombre es requerido")
    private String nombre;
    
    @NotBlank(message = "El apellido es requerido")
    private String apellido;
    
    private String telefono;
    private String email;
    
    private String estado;
    private String licenciaConducir;
    private String vehiculoAsignado;
    private String zonaAsignada;
    
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;
    
    private boolean activo;
    private Integer enviosAsignados;
    private Integer enviosCompletados;
}
