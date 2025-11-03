
package com.proyecto.OptiLogic.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue("CLIENTE")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Cliente extends Usuario {
    
    @Column(columnDefinition = "TEXT")
    private String direccion;
    
    @Column
    private String ciudad;
    
    @Column(name = "codigo_postal")
    private String codigoPostal;
    
    @JsonIgnore
    @OneToMany(mappedBy = "cliente")
    private List<Envio> envios;
    
    @Override
    public String getRol() {
        return "CLIENTE";
    }
}