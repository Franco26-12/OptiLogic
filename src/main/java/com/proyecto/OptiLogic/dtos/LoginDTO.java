package com.proyecto.OptiLogic.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginDTO {
    
    @NotBlank(message = "La cédula es requerida")
    @Pattern(regexp = "^[0-9]{6,12}$", message = "La cédula debe contener solo números")
    private String cedula;
    
    @NotBlank(message = "La contraseña es requerida")
    private String password;
}
