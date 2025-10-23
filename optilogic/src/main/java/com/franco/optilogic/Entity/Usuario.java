package com.franco.optilogic.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    
    private String rol;  

    public String getRol() {
        return this.rol;
    }

    
}
