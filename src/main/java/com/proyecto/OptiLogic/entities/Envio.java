package com.proyecto.OptiLogic.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String direccionDestino;
    private String estado; // Ej: "ASIGNADO", "EN_CAMINO", "ENTREGADO", "CONFIRMADO"

    @Column(updatable = false) // No se puede actualizar una vez creado
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    private LocalDateTime fechaConfirmacionCliente;

    // Relación: Muchos envíos pueden pertenecer a un cliente.
    @ManyToOne
    @JoinColumn(name = "cliente_id") // Clave foránea en la tabla Envio
    private Cliente cliente;

    // Relación: Muchos envíos pueden ser asignados a un repartidor.
    @ManyToOne
    @JoinColumn(name = "repartidor_id")
    private Repartidor repartidor;

    // Relación: Un envío puede contener muchos productos y un producto puede estar en muchos envíos.
    @ManyToMany
    @JoinTable(
        name = "envio_productos", // Nombre de la tabla intermedia
        joinColumns = @JoinColumn(name = "envio_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> productos;
}