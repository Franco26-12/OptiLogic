package com.proyecto.OptiLogic.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistroDTO {
    
    @NotBlank(message = "La cédula es requerida")
    @Pattern(regexp = "^[0-9]{6,12}$", message = "La cédula debe contener solo números")
    private String cedula;
    
    @NotBlank(message = "El nombre es requerido")
    private String nombre;
    
    @NotBlank(message = "El apellido es requerido")
    private String apellido;
    
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    private String telefono;
    private String email;
    
    private boolean esSuperAdmin = false;
}