package com.proyecto.OptiLogic.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue("REPARTIDOR")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Repartidor extends Usuario {
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRepartidor estado = EstadoRepartidor.DISPONIBLE;
    
    @Column(name = "licencia_conducir")
    private String licenciaConducir;
    
    @Column(name = "vehiculo_asignado")
    private String vehiculoAsignado;
    
    @Column(name = "zona_asignada")
    private String zonaAsignada;
    
    @ManyToOne
    @JoinColumn(name = "admin_creador_id")
    @JsonIgnore
    private Admin adminCreador;
    
    @OneToMany(mappedBy = "repartidor")
    @JsonIgnore
    private List<Envio> enviosAsignados;
    
    @Override
    public String getRol() {
        return "REPARTIDOR";
    }
    
    public enum EstadoRepartidor {
        DISPONIBLE,
        EN_RUTA,
        OCUPADO,
        DESCANSO,
        INACTIVO
    }
}


