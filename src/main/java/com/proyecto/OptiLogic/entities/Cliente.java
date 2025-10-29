
package com.proyecto.OptiLogic.entities;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {
    private String direccion;
    private String telefono;
}