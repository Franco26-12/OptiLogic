package com.proyecto.OptiLogic.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue("ADMIN")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends Usuario {
    
    @Column(name = "nivel_acceso")
    private Integer nivelAcceso = 1;
    
    @Column(name = "es_super_admin")
    private boolean esSuperAdmin = false;
    
    @JsonIgnore
    @OneToMany(mappedBy = "adminCreador")
    private List<Repartidor> repartidoresCreados;
    
    @JsonIgnore
    @OneToMany(mappedBy = "adminCreador")
    private List<Envio> enviosCreados;
    
    @Override
    public String getRol() {
        return "ADMIN";
    }
}