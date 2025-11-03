package com.proyecto.OptiLogic.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "envios")
@Data
@NoArgsConstructor
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String numeroSeguimiento;
    
    @Column(name = "codigo_qr", unique = true)
    private String codigoQR;

    @Column(nullable = false)
    private String direccionOrigen;
    
    @Column(nullable = false)
    private String direccionDestino;
    
    private String ciudadDestino;
    
    private String codigoPostalDestino;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEnvio estado = EstadoEnvio.PENDIENTE;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(nullable = false)
    private BigDecimal costoEnvio;
    
    @Column(nullable = false)
    private Integer prioridad = 1;

    @Column(updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    private LocalDateTime fechaAsignacion;
    
    private LocalDateTime fechaSalida;
    
    private LocalDateTime fechaEntregaEstimada;
    
    private LocalDateTime fechaEntregaReal;
    
    private LocalDateTime fechaConfirmacionCliente;
    
    private String firmaDigital;
    
    private String fotoEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "envios"})
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "enviosAsignados"})
    private Repartidor repartidor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_creador_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "enviosCreados"})
    private Admin adminCreador;

    @ManyToMany
    @JoinTable(
        name = "envio_productos",
        joinColumns = @JoinColumn(name = "envio_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<Producto> productos = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "envio_cantidades", joinColumns = @JoinColumn(name = "envio_id"))
    @MapKeyJoinColumn(name = "producto_id")
    @Column(name = "cantidad")
    private java.util.Map<Producto, Integer> cantidadPorProducto;
    
    public enum EstadoEnvio {
        PENDIENTE,
        ASIGNADO,
        EN_PREPARACION,
        LISTO_PARA_ENVIO,
        EN_TRANSITO,
        EN_REPARTO,
        ENTREGADO,
        NO_ENTREGADO,
        DEVUELTO,
        CANCELADO
    }
    
    @PrePersist
    public void prePersist() {
        if (this.numeroSeguimiento == null) {
            this.numeroSeguimiento = "ENV-" + System.currentTimeMillis();
        }
        if (this.codigoQR == null) {
            this.codigoQR = "QR-" + this.numeroSeguimiento;
        }
    }
}