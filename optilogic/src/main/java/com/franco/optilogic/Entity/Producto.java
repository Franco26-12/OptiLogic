package com.franco.optilogic.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sku;

    private String nombre;
    private String descripcion;
    
    @Column(nullable = false)
    private Integer stockDisponible = 0;
    
    @Column(nullable = false)
    private Integer stockMinimo = 0;
    
    @JsonIgnoreProperties({"productos"})
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public Integer getStockDisponible() {
		return stockDisponible;
	}
	
	public void setStockDisponible(Integer stockDisponible) {
		this.stockDisponible = stockDisponible;
	}
	
	public Integer getStockMinimo() {
		return stockMinimo;
	}
	
	public void setStockMinimo(Integer stockMinimo) {
		this.stockMinimo = stockMinimo;
	}
	
	public Categoria getCategoria() {
		return categoria;
	}
	
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
}