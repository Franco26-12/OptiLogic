package com.franco.optilogic.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 🚨 Generación automática de ID
    private Long id;

    @Column(unique = true, nullable = false) // 🚨 Email es único y obligatorio
    private String email;

    @Column(nullable = false) // 🚨 Password es obligatorio
    private String password;

    @Column(nullable = false)
    private String rol;

    // Constructor vacío (necesario para JPA)
    public Usuario() {
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
