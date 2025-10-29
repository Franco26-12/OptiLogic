// Repartidor.java
package com.proyecto.OptiLogic.entities;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
@DiscriminatorValue("REPARTIDOR")
public class Repartidor extends Usuario {
    private String estado; // Ej: "DISPONIBLE", "EN_RUTA"
}


