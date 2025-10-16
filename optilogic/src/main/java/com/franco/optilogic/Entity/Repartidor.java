package com.franco.optilogic.Entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
@DiscriminatorValue("REPARTIDOR")
public class Repartidor extends Usuario {
    private String estado;
}